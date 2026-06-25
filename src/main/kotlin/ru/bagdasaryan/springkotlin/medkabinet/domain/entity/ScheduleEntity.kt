package ru.bagdasaryan.springkotlin.medkabinet.domain

import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class ScheduleEntity(
    val id: ScheduleId,
    val doctorId: DoctorId,
    val workDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isPublished: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

