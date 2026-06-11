package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class SpecializationName(val value: String) {
    init {
        require(value.isNotBlank()) { "Название специализации обязательно" }
        require(value.length <= 255) { "Название специализации слишком длинное" }
    }
}
