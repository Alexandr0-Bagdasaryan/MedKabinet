package ru.bagdasaryan.springkotlin.medkabinet.domain
enum class AppointmentType(val value: String, val title: String) {
    PRIMARY("primary", "Первичный прием"),
    FOLLOW_UP("follow_up", "Повторный прием"),
    CONSULTATION("consultation", "Консультация"),
    EMERGENCY("emergency", "Срочный прием");

    companion object {
        fun from(value: String): AppointmentType {
            val normalized = value.trim()
            return entries.firstOrNull { it.value.equals(normalized, ignoreCase = true) || it.name.equals(normalized, ignoreCase = true) }
                ?: error("Недопустимый тип записи на прием: $value")
        }

        fun create(value: String): Result<AppointmentType> = runCatching { from(value) }
    }
}
