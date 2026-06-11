package ru.bagdasaryan.springkotlin.medkabinet.domain

@JvmInline
value class MedicalHistoryNote(val value: String) {
    companion object {
        fun create(raw: String): Result<MedicalHistoryNote> = runCatching {
            val normalized = raw.trim()
            require(normalized.length <= 1000) { "Примечание должно быть не длиннее 1000 символов" }
            MedicalHistoryNote(normalized)
        }
    }
}
