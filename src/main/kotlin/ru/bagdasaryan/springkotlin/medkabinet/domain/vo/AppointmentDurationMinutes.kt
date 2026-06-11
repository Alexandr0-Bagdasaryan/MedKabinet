package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class AppointmentDurationMinutes(val value: Short) {
    init { require(value in 5..180) { "Длительность приема должна быть в диапазоне 5..180 минут" } }
}
