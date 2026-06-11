package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class EventType(val value: String) {
    init { require(value.isNotBlank()) { "Тип события обязателен" } }
}
