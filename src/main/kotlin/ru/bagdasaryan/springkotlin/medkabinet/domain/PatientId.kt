package ru.bagdasaryan.springkotlin.medkabinet.domain

import java.io.Serializable

data class PatientId(val value: Int) : Serializable {
    init { require(value > 0) { "Идентификатор пациента должен быть положительным" } }

    companion object {
        fun create(value: Int): Result<PatientId> = runCatching { PatientId(value) }
    }
}
