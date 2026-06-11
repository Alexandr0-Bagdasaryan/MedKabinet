package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class InboxId(val value: Int) {
    init { require(value > 0) { "Идентификатор входящего сообщения должен быть положительным" } }
}
