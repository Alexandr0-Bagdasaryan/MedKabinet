package ru.bagdasaryan.springkotlin.medkabinet.storage.repository.impls

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.TimeSlotRepository
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.ScheduleId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeSlotStatus
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.TimeSlots

@Repository
class TimeSlotRepositoryImpl : TimeSlotRepository {
    override suspend fun findAvailableById(id: TimeSlotId): Result<TimeSlotEntity?> = runCatching {
        newSuspendedTransaction {
            TimeSlots
                .selectAll()
                .where { (TimeSlots.id eq id.value) and (TimeSlots.status eq TimeSlotStatus.AVAILABLE.value) }
                .singleOrNull()
                ?.let { row ->
                    TimeSlotEntity(
                        id = TimeSlotId(row[TimeSlots.id].value),
                        scheduleId = ScheduleId(row[TimeSlots.scheduleId]),
                        doctorId = DoctorId(row[TimeSlots.doctorId]),
                        slotDate = row[TimeSlots.slotDate],
                        startTime = row[TimeSlots.startTime],
                        endTime = row[TimeSlots.endTime],
                        status = TimeSlotStatus.from(row[TimeSlots.status]),
                        createdAt = row[TimeSlots.createdAt],
                        updatedAt = row[TimeSlots.updatedAt]
                    )
                }
        }
    }
}
