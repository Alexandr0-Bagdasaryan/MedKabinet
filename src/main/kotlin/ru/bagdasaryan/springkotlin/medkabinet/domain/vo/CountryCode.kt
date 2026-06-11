package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class CountryCode(val value: String) {
    init {
        require(value.matches(Regex("^[A-Z]{2}$"))) { "Код страны должен быть в формате ISO-2 в верхнем регистре" }
    }
}
