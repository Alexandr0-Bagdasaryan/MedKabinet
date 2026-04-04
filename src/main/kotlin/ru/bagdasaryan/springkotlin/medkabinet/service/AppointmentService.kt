package ru.bagdasaryan.springkotlin.medkabinet.service

import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.AppointmentPersister

@Service
class AppointmentService(
    private val appointmentPersister: AppointmentPersister
) {
    suspend fun createAppointment(
        patientId: Int,
        timeSlotId: Int,
        appointmentType: String,
        notes: String?
    ) = appointmentPersister.createAppointment(
        patientId = patientId,
        timeSlotId = timeSlotId,
        appointmentType = appointmentType,
        notes = notes
    )
}
