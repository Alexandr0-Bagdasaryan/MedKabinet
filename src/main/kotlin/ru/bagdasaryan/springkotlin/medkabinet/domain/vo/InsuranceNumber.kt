package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class InsuranceNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Номер страховки обязателен" }
        require(value.length <= 128) { "Номер страховки слишком длинный" }
    }
}
