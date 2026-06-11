package ru.bagdasaryan.springkotlin.medkabinet.domain

@JvmInline
value class PatientMedicalHistoryId(val value: Int) {
    companion object {
        fun create(value: Int): Result<PatientMedicalHistoryId> = runCatching {
            require(value > 0) { "Идентификатор записи истории должен быть положительным" }
            PatientMedicalHistoryId(value)
        }
    }
}
