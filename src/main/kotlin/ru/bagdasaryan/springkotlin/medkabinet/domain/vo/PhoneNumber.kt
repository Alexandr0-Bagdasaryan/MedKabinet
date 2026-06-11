package ru.bagdasaryan.springkotlin.medkabinet.domain

@JvmInline
value class PhoneNumber(val value: String) {
    init {
        val normalized = value.trim()
        require(normalized.isNotBlank()) { "Телефон обязателен" }
        require(normalized.none(Char::isWhitespace)) { "Телефон не должен содержать пробелы" }
        require(normalized.length in 5..32) { "Телефон имеет некорректную длину" }
    }
}
