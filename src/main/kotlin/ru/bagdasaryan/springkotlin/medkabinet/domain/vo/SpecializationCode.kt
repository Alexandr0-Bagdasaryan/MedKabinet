package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class SpecializationCode(val value: String) {
    init {
        require(value.isNotBlank()) { "Код специализации обязателен" }
        require(value.length <= 64) { "Код специализации слишком длинный" }
    }
}
