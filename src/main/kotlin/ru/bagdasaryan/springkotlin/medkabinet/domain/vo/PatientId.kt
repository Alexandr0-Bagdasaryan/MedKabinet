package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class PatientId(val value: Int) {
    init { require(value > 0) { "Идентификатор пациента должен быть положительным" } }

    companion object {
        fun create(value: Int): Result<PatientId> = runCatching { PatientId(value) }
    }
}
