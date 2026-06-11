package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class OutboxId(val value: Int) {
    init { require(value > 0) { "Идентификатор исходящего сообщения должен быть положительным" } }
}
