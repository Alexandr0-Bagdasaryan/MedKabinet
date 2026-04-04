package ru.bagdasaryan.springkotlin.medkabinet.storage.persister

import org.springframework.stereotype.Repository

@Repository
interface AppointmentPersister {
    suspend fun createAppointment(
        patientId: Int,
        timeSlotId: Int,
        appointmentType: String,
        notes: String?
    ): Result<Unit>
}
