package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class DepartmentCode(val value: String) {
    init {
        require(value.isNotBlank()) { "Код отделения обязателен" }
        require(value.length <= 64) { "Код отделения слишком длинный" }
    }
}
