package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class DepartmentId(val value: Int) {
    init { require(value > 0) { "Идентификатор отделения должен быть положительным" } }
}
