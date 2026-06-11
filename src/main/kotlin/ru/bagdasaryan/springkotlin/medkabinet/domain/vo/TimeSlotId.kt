package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class TimeSlotId(val value: Int) {
    init { require(value > 0) { "Идентификатор временного слота должен быть положительным" } }

    companion object {
        fun create(value: Int): Result<TimeSlotId> = runCatching { TimeSlotId(value) }
    }
}
