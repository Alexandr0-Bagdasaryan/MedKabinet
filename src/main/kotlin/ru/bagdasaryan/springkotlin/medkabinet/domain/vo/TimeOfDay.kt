package ru.bagdasaryan.springkotlin.medkabinet.domain.vo

import java.time.LocalTime

@JvmInline
value class TimeOfDay private constructor(val value: LocalTime) {
    companion object {
        fun of(value: LocalTime): TimeOfDay = TimeOfDay(value)
        fun create(value: LocalTime): Result<TimeOfDay> = runCatching { of(value) }
    }
}
