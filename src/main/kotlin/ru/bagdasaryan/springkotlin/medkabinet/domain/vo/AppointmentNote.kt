package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class AppointmentNote(val value: String) {
    init { require(value.isNotBlank()) { "Примечание к записи не должно быть пустым" } }

    companion object {
        fun create(value: String): Result<AppointmentNote> = runCatching { AppointmentNote(value) }
    }
}
