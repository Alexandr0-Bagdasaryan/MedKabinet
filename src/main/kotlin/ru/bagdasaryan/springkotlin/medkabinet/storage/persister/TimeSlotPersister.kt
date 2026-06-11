package ru.bagdasaryan.springkotlin.medkabinet.storage.persister

import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId

@Repository
interface TimeSlotPersister {
    suspend fun markBooked(id: TimeSlotId): Result<Unit>
}
