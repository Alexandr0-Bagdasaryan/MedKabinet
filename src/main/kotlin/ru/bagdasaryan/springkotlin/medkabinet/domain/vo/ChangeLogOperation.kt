package ru.bagdasaryan.springkotlin.medkabinet.domain
enum class ChangeLogOperation(val value: String, val title: String) {
    INSERT("insert", "Создание"),
    UPDATE("update", "Изменение"),
    DELETE("delete", "Удаление");

    companion object {
        fun from(value: String): ChangeLogOperation {
            val normalized = value.trim()
            return entries.firstOrNull { it.value.equals(normalized, ignoreCase = true) || it.name.equals(normalized, ignoreCase = true) }
                ?: error("Недопустимая операция журнала изменений: $value")
        }
    }
}
