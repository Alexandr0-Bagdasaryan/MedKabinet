package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class InboxError(val value: String) {
    init { require(value.isNotBlank()) { "Ошибка входящего сообщения не должна быть пустой" } }
}
