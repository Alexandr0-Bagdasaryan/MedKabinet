package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class AddressCity(val value: String) {
    init { require(value.isNotBlank()) { "Город адреса обязателен" } }
}
