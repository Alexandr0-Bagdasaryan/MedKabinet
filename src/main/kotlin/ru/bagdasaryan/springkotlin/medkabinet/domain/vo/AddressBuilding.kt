package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class AddressBuilding(val value: String) {
    init { require(value.isNotBlank()) { "Номер дома обязателен" } }
}
