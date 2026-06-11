package ru.bagdasaryan.springkotlin.medkabinet.domain
enum class PatientGender(val value: String, val title: String) {
    MALE("male", "Мужской"),
    FEMALE("female", "Женский");

    companion object {
        fun from(value: String): PatientGender {
            val normalized = value.trim()
            return entries.firstOrNull { it.value.equals(normalized, ignoreCase = true) || it.name.equals(normalized, ignoreCase = true) }
                ?: error("Недопустимый пол пациента: $value")
        }
    }
}
