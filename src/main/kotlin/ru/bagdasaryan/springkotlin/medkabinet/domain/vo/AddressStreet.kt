package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class AddressStreet(val value: String) {
    init { require(value.isNotBlank()) { "Улица адреса обязательна" } }
}
