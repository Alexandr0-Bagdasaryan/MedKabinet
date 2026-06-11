package ru.bagdasaryan.springkotlin.medkabinet.domain
enum class AppointmentStatus(val value: String, val title: String) {
    SCHEDULED("scheduled", "Запланирована"),
    COMPLETED("completed", "Завершена"),
    CANCELLED("cancelled", "Отменена"),
    NO_SHOW("no_show", "Неявка");

    companion object {
        fun from(value: String): AppointmentStatus {
            val normalized = value.trim()
            return entries.firstOrNull { it.value.equals(normalized, ignoreCase = true) || it.name.equals(normalized, ignoreCase = true) }
                ?: error("Недопустимый статус записи на прием: $value")
        }

        fun create(value: String): Result<AppointmentStatus> = runCatching { from(value) }
    }
}
