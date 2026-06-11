package ru.bagdasaryan.springkotlin.medkabinet.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class InboxEntity(
    val id: InboxId,
    val messageId: MessageId,
    val sourceService: SourceService,
    val eventType: EventType,
    val payload: JsonPayload,
    val status: InboxStatus,
    val receivedAt: LocalDateTime,
    val processedAt: LocalDateTime?,
    val error: InboxError?
)

