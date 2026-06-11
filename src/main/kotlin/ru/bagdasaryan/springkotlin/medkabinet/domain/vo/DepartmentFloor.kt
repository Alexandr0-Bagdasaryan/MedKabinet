package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class DepartmentFloor(val value: Short) {
    init { require(value in 1..99) { "Этаж отделения должен быть в диапазоне 1..99" } }
}
