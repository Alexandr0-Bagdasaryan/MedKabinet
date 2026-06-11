package ru.bagdasaryan.springkotlin.medkabinet.domain
enum class OutboxStatus(val value: String, val title: String) {
    PENDING("pending", "В ожидании"),
    PUBLISHED("published", "Опубликовано"),
    FAILED("failed", "Ошибка");

    companion object {
        fun from(value: String): OutboxStatus {
            val normalized = value.trim()
            return entries.firstOrNull { it.value.equals(normalized, ignoreCase = true) || it.name.equals(normalized, ignoreCase = true) }
                ?: error("Недопустимый статус исходящего сообщения: $value")
        }
    }
}
