package ru.bagdasaryan.springkotlin.medkabinet.storage.persister.impls

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Appointments
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Patients
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.TimeSlots
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.AppointmentPersister

@Repository
class AppointmentPersisterImpl : AppointmentPersister {
    override suspend fun createAppointment(
        patientId: Int,
        timeSlotId: Int,
        appointmentType: String,
        notes: String?
    ): Result<Unit> = runCatching {
        newSuspendedTransaction {
            require(appointmentType.isNotBlank()) { "Тип приема обязателен" }

            val slot = TimeSlots
                .join(
                    Doctors,
                    JoinType.INNER,
                    TimeSlots.doctorId,
                    Doctors.id
                )
                .selectAll()
                .where { (TimeSlots.id eq timeSlotId) and (TimeSlots.status eq "AVAILABLE") }
                .singleOrNull()
                ?: error("Выбранный слот недоступен")

            val patientExists = Patients.selectAll().where { Patients.id eq patientId }.any()
            require(patientExists) { "Пациент не найден" }

            Appointments.insert {
                it[Appointments.patientId] = patientId
                it[Appointments.doctorId] = slot[TimeSlots.doctorId]
                it[Appointments.timeSlotId] = slot[TimeSlots.id].value
                it[Appointments.appointmentDate] = slot[TimeSlots.slotDate]
                it[Appointments.startTime] = slot[TimeSlots.startTime]
                it[Appointments.endTime] = slot[TimeSlots.endTime]
                it[Appointments.appointmentType] = appointmentType.trim()
                it[Appointments.status] = "SCHEDULED"
                it[Appointments.notes] = notes?.trim()?.ifBlank { null }
            }

            TimeSlots.update({ TimeSlots.id eq timeSlotId }) {
                it[status] = "BOOKED"
            }
        }
    }
}
