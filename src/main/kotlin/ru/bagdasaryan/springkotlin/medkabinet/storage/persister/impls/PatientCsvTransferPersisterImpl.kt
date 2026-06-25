package ru.bagdasaryan.springkotlin.medkabinet.storage.persister.impls

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.ScheduleId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeSlotStatus
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.PatientCsvTransferPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Appointments
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Schedules
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.TimeSlots

@Repository
class PatientCsvTransferPersisterImpl : PatientCsvTransferPersister {
    override suspend fun importAppointments(
        patientId: PatientId,
        appointments: List<PatientCsvTransferPersister.ImportedAppointmentRow>
    ): Result<Unit> = runCatching {
        newSuspendedTransaction {
            appointments.forEach { row ->
                val scheduleId = findOrCreateScheduleId(row)
                val timeSlotId = findOrCreateTimeSlotId(scheduleId, row)
                if (!appointmentExists(patientId, timeSlotId, row)) {
                    Appointments.insert {
                        it[Appointments.patientId] = patientId.value
                        it[Appointments.doctorId] = row.doctorId.value
                        it[Appointments.timeSlotId] = timeSlotId.value
                        it[Appointments.appointmentDate] = row.appointmentDate
                        it[Appointments.startTime] = row.startTime
                        it[Appointments.endTime] = row.endTime
                        it[Appointments.appointmentType] = row.appointmentType.value
                        it[Appointments.status] = row.appointmentStatus.value
                        it[Appointments.notes] = row.appointmentNote?.value
                    }
                }
            }
        }
    }

    private fun org.jetbrains.exposed.sql.Transaction.findOrCreateScheduleId(
        row: PatientCsvTransferPersister.ImportedAppointmentRow
    ): ScheduleId {
        val existingId = Schedules.selectAll()
            .where { (Schedules.doctorId eq row.doctorId.value) and (Schedules.workDate eq row.appointmentDate) }
            .singleOrNull()
            ?.get(Schedules.id)
            ?.value
        if (existingId != null) {
            return ScheduleId(existingId)
        }

        val newId = Schedules.insertAndGetId {
            it[doctorId] = row.doctorId.value
            it[workDate] = row.appointmentDate
            it[startTime] = row.startTime
            it[endTime] = row.endTime
            it[isPublished] = true
        }
        return ScheduleId(newId.value)
    }

    private fun org.jetbrains.exposed.sql.Transaction.findOrCreateTimeSlotId(
        scheduleId: ScheduleId,
        row: PatientCsvTransferPersister.ImportedAppointmentRow
    ): TimeSlotId {
        val existing = TimeSlots.selectAll()
            .where {
                (TimeSlots.doctorId eq row.doctorId.value) and
                    (TimeSlots.slotDate eq row.appointmentDate) and
                    (TimeSlots.startTime eq row.startTime) and
                    (TimeSlots.endTime eq row.endTime)
            }
            .singleOrNull()

        if (existing != null) {
            val existingId = TimeSlotId(existing[TimeSlots.id].value)
            if (existing[TimeSlots.status] != TimeSlotStatus.BOOKED.value) {
                TimeSlots.update({ TimeSlots.id eq existingId.value }) {
                    it[status] = TimeSlotStatus.BOOKED.value
                }
            }
            return existingId
        }

        val newId = TimeSlots.insertAndGetId {
            it[TimeSlots.scheduleId] = scheduleId.value
            it[doctorId] = row.doctorId.value
            it[slotDate] = row.appointmentDate
            it[startTime] = row.startTime
            it[endTime] = row.endTime
            it[status] = TimeSlotStatus.BOOKED.value
        }
        return TimeSlotId(newId.value)
    }

    private fun org.jetbrains.exposed.sql.Transaction.appointmentExists(
        patientId: PatientId,
        timeSlotId: TimeSlotId,
        row: PatientCsvTransferPersister.ImportedAppointmentRow
    ): Boolean =
        Appointments.selectAll()
            .where {
                (Appointments.patientId eq patientId.value) and
                    (Appointments.timeSlotId eq timeSlotId.value) and
                    (Appointments.doctorId eq row.doctorId.value) and
                    (Appointments.appointmentDate eq row.appointmentDate) and
                    (Appointments.startTime eq row.startTime) and
                    (Appointments.endTime eq row.endTime) and
                    (Appointments.appointmentType eq row.appointmentType.value) and
                    (Appointments.status eq row.appointmentStatus.value)
            }
            .any { existing ->
                (existing[Appointments.notes] ?: "") == (row.appointmentNote?.value ?: "")
            }
}
