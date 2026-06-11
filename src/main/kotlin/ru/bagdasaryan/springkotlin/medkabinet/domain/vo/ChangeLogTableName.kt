package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class ChangeLogTableName(val value: String) {
    init { require(value.isNotBlank()) { "Название таблицы журнала изменений обязательно" } }
}
