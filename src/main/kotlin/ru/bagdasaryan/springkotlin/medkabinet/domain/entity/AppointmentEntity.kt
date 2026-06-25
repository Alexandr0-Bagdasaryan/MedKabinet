package ru.bagdasaryan.springkotlin.medkabinet.domain

import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class AppointmentEntity(
    val id: AppointmentId,
    val patientId: PatientId,
    val doctorId: DoctorId,
    val timeSlotId: TimeSlotId,
    val appointmentDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val appointmentType: AppointmentType,
    val status: AppointmentStatus,
    val cancellationReason: CancellationReason?,
    val cancelledAt: LocalDateTime?,
    val cancelledBy: CancelledBy?,
    val notes: AppointmentNote?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

