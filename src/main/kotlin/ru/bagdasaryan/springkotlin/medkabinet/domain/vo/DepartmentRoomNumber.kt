package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class DepartmentRoomNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Номер кабинета обязателен" }
        require(value.length <= 32) { "Номер кабинета слишком длинный" }
    }
}
