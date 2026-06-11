package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class SourceService(val value: String) {
    init { require(value.isNotBlank()) { "Служба-источник обязательна" } }
}
