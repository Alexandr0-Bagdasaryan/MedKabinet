package ru.bagdasaryan.springkotlin.medkabinet.domain.vo
@JvmInline
value class MedicalCardNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Номер медицинской карты обязателен" }
        require(value.length <= 128) { "Номер медицинской карты слишком длинный" }
    }
}
