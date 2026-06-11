package ru.bagdasaryan.springkotlin.medkabinet.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class OutboxEntity(
    val id: OutboxId,
    val aggregateType: AggregateType,
    val aggregateId: AggregateEntityId,
    val eventType: EventType,
    val payload: JsonPayload,
    val status: OutboxStatus,
    val idempotencyKey: IdempotencyKey,
    val occurredAt: LocalDateTime,
    val publishedAt: LocalDateTime?,
    val attempts: OutboxAttempts,
    val lastError: OutboxLastError?
)

