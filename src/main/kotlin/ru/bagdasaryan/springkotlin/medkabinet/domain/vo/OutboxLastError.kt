package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class OutboxLastError(val value: String) {
    init { require(value.isNotBlank()) { "Поле последней ошибки исходящего сообщения не должно быть пустым" } }
}
