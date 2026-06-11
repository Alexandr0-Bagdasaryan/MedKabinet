package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class PatientEmail(val value: String) {
    init {
        require(value.contains("@")) { "Электронная почта пациента должна содержать @" }
        require(value.length <= 255) { "Электронная почта пациента слишком длинная" }
    }
}
