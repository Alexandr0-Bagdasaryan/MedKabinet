package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class ChangeLogId(val value: Long) {
    init { require(value > 0) { "Идентификатор журнала изменений должен быть положительным" } }
}
