package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class AggregateEntityId(val value: Int) {
    init { require(value > 0) { "Идентификатор сущности агрегата должен быть положительным" } }
}
