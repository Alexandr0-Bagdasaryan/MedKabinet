package ru.bagdasaryan.springkotlin.medkabinet.domain

@JvmInline
value class MedicalHistoryDiagnosis(val value: String) {
    companion object {
        fun create(raw: String): Result<MedicalHistoryDiagnosis> = runCatching {
            val normalized = raw.trim()
            require(normalized.isNotBlank()) { "Диагноз обязателен" }
            require(normalized.length in 2..500) { "Диагноз должен содержать от 2 до 500 символов" }
            MedicalHistoryDiagnosis(normalized)
        }
    }
}
