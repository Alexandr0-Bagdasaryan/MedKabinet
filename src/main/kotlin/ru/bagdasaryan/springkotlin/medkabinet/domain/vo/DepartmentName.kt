package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class DepartmentName(val value: String) {
    init {
        require(value.isNotBlank()) { "Название отделения обязательно" }
        require(value.length <= 255) { "Название отделения слишком длинное" }
    }
}
