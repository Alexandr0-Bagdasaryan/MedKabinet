package ru.bagdasaryan.springkotlin.medkabinet.domain
enum class InboxStatus(val value: String, val title: String) {
    RECEIVED("received", "Получено"),
    PROCESSED("processed", "Обработано"),
    FAILED("failed", "Ошибка");

    companion object {
        fun from(value: String): InboxStatus {
            val normalized = value.trim()
            return entries.firstOrNull { it.value.equals(normalized, ignoreCase = true) || it.name.equals(normalized, ignoreCase = true) }
                ?: error("Недопустимый статус входящего сообщения: $value")
        }
    }
}
