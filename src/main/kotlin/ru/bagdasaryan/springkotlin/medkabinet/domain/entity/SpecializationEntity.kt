package ru.bagdasaryan.springkotlin.medkabinet.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class SpecializationEntity(
    val id: SpecializationId,
    val code: SpecializationCode,
    val name: SpecializationName,
    val createdAt: LocalDateTime
)

