package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class IdempotencyKey(val value: String) {
    init { require(value.isNotBlank()) { "Ключ идемпотентности обязателен" } }
}
