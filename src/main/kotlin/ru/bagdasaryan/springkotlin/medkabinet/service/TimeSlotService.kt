package ru.bagdasaryan.springkotlin.medkabinet.service

import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.TimeSlotPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.TimeSlotRepository

@Service
class TimeSlotService(
    private val timeSlotRepository: TimeSlotRepository,
    private val timeSlotPersister: TimeSlotPersister
) {
    suspend fun findAvailableById(id: TimeSlotId): Result<TimeSlotEntity?> =
        timeSlotRepository.findAvailableById(id)

    suspend fun markBooked(id: TimeSlotId): Result<Unit> =
        timeSlotPersister.markBooked(id)
}
