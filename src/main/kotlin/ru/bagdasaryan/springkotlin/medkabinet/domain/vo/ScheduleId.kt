package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class ScheduleId(val value: Int) {
    init { require(value > 0) { "Идентификатор расписания должен быть положительным" } }
}
