package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class ChangeLogChangedBy(val value: String) {
    init { require(value.isNotBlank()) { "Поле changedBy в журнале изменений не должно быть пустым" } }
}
