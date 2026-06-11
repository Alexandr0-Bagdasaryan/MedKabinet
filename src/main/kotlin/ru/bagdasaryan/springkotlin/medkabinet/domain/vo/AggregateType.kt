package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class AggregateType(val value: String) {
    init { require(value.isNotBlank()) { "Тип агрегата обязателен" } }
}
