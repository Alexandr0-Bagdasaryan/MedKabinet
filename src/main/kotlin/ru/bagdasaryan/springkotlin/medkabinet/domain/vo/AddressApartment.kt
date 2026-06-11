package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class AddressApartment(val value: String) {
    init { require(value.isNotBlank()) { "Квартира адреса не должна быть пустой" } }
}
