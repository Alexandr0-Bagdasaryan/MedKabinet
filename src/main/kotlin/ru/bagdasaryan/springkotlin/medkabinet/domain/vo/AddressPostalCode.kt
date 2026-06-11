package ru.bagdasaryan.springkotlin.medkabinet.domain.vo
@JvmInline
value class AddressPostalCode(val value: String) {
    init { require(value.isNotBlank()) { "Почтовый индекс не должен быть пустым" } }
}
