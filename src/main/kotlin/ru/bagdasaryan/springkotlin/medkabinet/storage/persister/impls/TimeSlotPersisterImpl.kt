package ru.bagdasaryan.springkotlin.medkabinet.storage.persister.impls

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.TimeSlotPersister
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.TimeSlots

@Repository
class TimeSlotPersisterImpl : TimeSlotPersister {
    override suspend fun markBooked(id: TimeSlotId): Result<Unit> = runCatching {
        val updated = newSuspendedTransaction {
            TimeSlots.update({ TimeSlots.id eq id.value }) {
                it[status] = "booked"
            }
        }
        require(updated > 0) { "Не удалось обновить слот" }
    }
}
