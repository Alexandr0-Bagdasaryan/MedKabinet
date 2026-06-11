package ru.bagdasaryan.springkotlin.medkabinet.storage.repository

import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.ScheduleId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeSlotStatus

@Repository
interface TimeSlotRepository {
    suspend fun findAvailableById(id: TimeSlotId): Result<TimeSlotEntity?>
}
