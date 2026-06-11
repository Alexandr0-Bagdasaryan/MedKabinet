package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class InsuranceCompany(val value: String) {
    init { require(value.isNotBlank()) { "Страховая компания обязательна" } }
}
