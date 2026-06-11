package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class DoctorEmail(val value: String) {
    init {
        require(value.contains("@")) { "Электронная почта врача должна содержать @" }
        require(value.length <= 255) { "Электронная почта врача слишком длинная" }
    }
}
