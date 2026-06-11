package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class DoctorId(val value: Int) {
    init { require(value > 0) { "Идентификатор врача должен быть положительным" } }
}
