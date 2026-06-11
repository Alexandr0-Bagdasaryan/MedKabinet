package ru.bagdasaryan.springkotlin.medkabinet.storage.persister.impls

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentType
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotEntity
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Appointments
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.AppointmentPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.AppointmentPersister.CreateAppointmentDTO

@Repository
class AppointmentPersisterImpl : AppointmentPersister {
    override suspend fun createAppointment(dto: CreateAppointmentDTO): Result<Unit> = runCatching {
        newSuspendedTransaction {
            Appointments.insert {
                it[Appointments.patientId] = dto.patientId.value
                it[Appointments.doctorId] = dto.timeSlot.doctorId.value
                it[Appointments.timeSlotId] = dto.timeSlot.id.value
                it[Appointments.appointmentDate] = dto.timeSlot.slotDate
                it[Appointments.startTime] = dto.timeSlot.startTime
                it[Appointments.endTime] = dto.timeSlot.endTime
                it[Appointments.appointmentType] = dto.appointmentType.value
                it[Appointments.status] = dto.appointmentStatus.value
                it[Appointments.notes] = dto.notes?.value
            }
        }
    }
}
