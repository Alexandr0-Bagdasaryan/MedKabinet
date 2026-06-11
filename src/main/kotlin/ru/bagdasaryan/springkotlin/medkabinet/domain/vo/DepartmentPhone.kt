package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class DepartmentPhone(val value: String) {
    init {
        require(value.isNotBlank()) { "Телефон отделения обязателен" }
        require(value.length in 5..32) { "Телефон отделения имеет некорректную длину" }
    }
}
