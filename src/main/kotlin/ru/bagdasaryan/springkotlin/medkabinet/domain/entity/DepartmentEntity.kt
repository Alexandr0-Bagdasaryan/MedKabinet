package ru.bagdasaryan.springkotlin.medkabinet.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class DepartmentEntity(
    val id: DepartmentId,
    val code: DepartmentCode,
    val name: DepartmentName,
    val floor: DepartmentFloor?,
    val roomNumber: DepartmentRoomNumber?,
    val phone: DepartmentPhone?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

