package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class JsonPayload(val value: String) {
    init { require(value.isNotBlank()) { "JSON-полезная нагрузка обязательна" } }
}
