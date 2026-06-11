package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class CancelledBy(val value: String) {
    init { require(value.isNotBlank()) { "Поле cancelledBy не должно быть пустым" } }
}
