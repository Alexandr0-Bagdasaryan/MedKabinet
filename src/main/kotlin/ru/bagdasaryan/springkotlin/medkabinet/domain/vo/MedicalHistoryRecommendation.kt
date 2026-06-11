package ru.bagdasaryan.springkotlin.medkabinet.domain

@JvmInline
value class MedicalHistoryRecommendation(val value: String) {
    companion object {
        fun create(raw: String): Result<MedicalHistoryRecommendation> = runCatching {
            val normalized = raw.trim()
            require(normalized.isNotBlank()) { "Рекомендация обязательна" }
            require(normalized.length in 2..1000) { "Рекомендация должна содержать от 2 до 1000 символов" }
            MedicalHistoryRecommendation(normalized)
        }
    }
}
