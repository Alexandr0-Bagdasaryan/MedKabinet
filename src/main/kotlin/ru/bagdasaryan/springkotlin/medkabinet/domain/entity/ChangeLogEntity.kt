package ru.bagdasaryan.springkotlin.medkabinet.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class ChangeLogEntity(
    val id: ChangeLogId,
    val tableName: ChangeLogTableName,
    val recordId: ChangeLogRecordId,
    val operation: ChangeLogOperation,
    val oldValues: JsonPayload?,
    val newValues: JsonPayload?,
    val changedBy: ChangeLogChangedBy?,
    val changedAt: LocalDateTime
)
