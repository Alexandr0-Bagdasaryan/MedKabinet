package ru.bagdasaryan.springkotlin.medkabinet.domain.vo

enum class TimeSlotStatus(val value: String, val title: String) {
    AVAILABLE("available", "Свободен"),
    BOOKED("booked", "Занят"),
    BLOCKED("blocked", "Заблокирован");

    companion object {
        fun from(value: String): TimeSlotStatus {
            val normalized = value.trim()
            return entries.firstOrNull {
                it.value.equals(normalized, ignoreCase = true) || it.name.equals(
                    normalized,
                    ignoreCase = true
                )
            }
                ?: error("Недопустимый статус временного слота: $value")
        }
    }
}
