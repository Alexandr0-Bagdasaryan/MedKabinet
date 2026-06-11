package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class ChangeLogRecordId(val value: Int) {
    init { require(value > 0) { "Идентификатор записи журнала изменений должен быть положительным" } }
}
