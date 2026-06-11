package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class SpecializationId(val value: Int) {
    init { require(value > 0) { "Идентификатор специализации должен быть положительным" } }
}
