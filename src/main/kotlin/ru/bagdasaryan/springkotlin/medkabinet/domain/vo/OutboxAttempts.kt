package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class OutboxAttempts(val value: Short) {
    init { require(value >= 0) { "Количество попыток исходящего сообщения должно быть неотрицательным" } }
}
