package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class DoctorLicenseNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Номер лицензии врача обязателен" }
        require(value.length <= 64) { "Номер лицензии врача слишком длинный" }
    }
}
