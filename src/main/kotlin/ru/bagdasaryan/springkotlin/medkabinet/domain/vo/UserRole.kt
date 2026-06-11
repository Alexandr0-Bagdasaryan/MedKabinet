package ru.bagdasaryan.springkotlin.medkabinet.domain

enum class UserRole(val value: String, val title: String) {
    ADMIN("admin", "Администратор"),
    DOCTOR("doctor", "Врач"),
    PATIENT("patient", "Пациент");

    companion object {
        fun create(value: String): Result<UserRole> = runCatching { from(value) }

        fun from(value: String): UserRole =
            entries.firstOrNull { role ->
                role.value.equals(value, ignoreCase = true) || role.name.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException("Неизвестная роль пользователя: $value")
    }
}
