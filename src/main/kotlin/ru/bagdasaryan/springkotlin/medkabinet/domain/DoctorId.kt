package ru.bagdasaryan.springkotlin.medkabinet.domain

import java.io.Serializable

data class DoctorId(val value: Int) : Serializable {
    init { require(value > 0) { "Идентификатор врача должен быть положительным" } }
}
