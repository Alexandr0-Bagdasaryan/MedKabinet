package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class MessageId(val value: String) {
    init { require(value.isNotBlank()) { "Идентификатор сообщения обязателен" } }
}
