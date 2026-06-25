package ru.bagdasaryan.springkotlin.medkabinet.storage.repository

import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotEntity

@Repository
interface TimeSlotRepository {
    suspend fun findAvailableById(id: TimeSlotId): Result<TimeSlotEntity?>
}
