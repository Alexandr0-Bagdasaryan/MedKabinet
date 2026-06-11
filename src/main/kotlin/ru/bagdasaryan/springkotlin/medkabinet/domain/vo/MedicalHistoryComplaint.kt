package ru.bagdasaryan.springkotlin.medkabinet.domain

@JvmInline
value class MedicalHistoryComplaint(val value: String) {
    companion object {
        fun create(raw: String): Result<MedicalHistoryComplaint> = runCatching {
            val normalized = raw.trim()
            require(normalized.isNotBlank()) { "Жалоба обязательна" }
            require(normalized.length in 2..500) { "Жалоба должна содержать от 2 до 500 символов" }
            MedicalHistoryComplaint(normalized)
        }
    }
}
