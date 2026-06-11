package ru.bagdasaryan.springkotlin.medkabinet.storage.persister

import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentType
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotEntity

@Repository
interface AppointmentPersister {
    suspend fun createAppointment(dto: CreateAppointmentDTO): Result<Unit>

    class CreateAppointmentDTO(
        val patientId: PatientId,
        val timeSlot: TimeSlotEntity,
        val appointmentType: AppointmentType,
        val appointmentStatus: AppointmentStatus,
        val notes: AppointmentNote?
    )
}
