package ru.bagdasaryan.springkotlin.medkabinet.domain

import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeSlotStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class TimeSlotEntity(
    val id: TimeSlotId,
    val scheduleId: ScheduleId,
    val doctorId: DoctorId,
    val slotDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val status: TimeSlotStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

