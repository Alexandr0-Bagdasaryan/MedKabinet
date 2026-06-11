package ru.bagdasaryan.springkotlin.medkabinet.domain.vo

enum class DoctorActivityStatus(val value: String, val title: String) {
    ACTIVE("active", "Активен"),
    INACTIVE("inactive", "Неактивен");

    companion object {
        fun from(value: Boolean): DoctorActivityStatus = if (value) ACTIVE else INACTIVE

        fun from(value: String): DoctorActivityStatus {
            val normalized = value.trim()
            return entries.firstOrNull {
                it.value.equals(normalized, ignoreCase = true) || it.name.equals(
                    normalized,
                    ignoreCase = true
                )
            } ?: error("Недопустимый статус активности врача: $value")
        }

        fun create(value: String): Result<DoctorActivityStatus> = runCatching { from(value) }
    }
}
