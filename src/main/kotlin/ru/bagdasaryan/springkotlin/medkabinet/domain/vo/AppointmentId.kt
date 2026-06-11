package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class AppointmentId(val value: Int) {
    init { require(value > 0) { "Идентификатор записи на прием должен быть положительным" } }
}
