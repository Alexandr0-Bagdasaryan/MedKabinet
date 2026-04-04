package ru.bagdasaryan.springkotlin.medkabinet.storage.query

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.TimeSlots

@Component
class FindAvailableSlotsQuery {

    suspend fun execute(): Result<List<TimeSlotOptionDTO>> = runCatching {
        newSuspendedTransaction {
            TimeSlots
                .join(
                    Doctors,
                    JoinType.INNER,
                    TimeSlots.doctorId,
                    Doctors.id
                )
                .selectAll()
                .where { TimeSlots.status eq "AVAILABLE" }
                .orderBy(TimeSlots.slotDate to SortOrder.ASC)
                .orderBy(TimeSlots.startTime to SortOrder.ASC)
                .limit(1000)
                .map { it.toDTO() }
        }
    }

    class TimeSlotOptionDTO(
        val id: String,
        val doctorId: String,
        val doctorFio: String,
        val slotDate: String,
        val startTime: String,
        val endTime: String
    )

    private fun ResultRow.toDTO(): TimeSlotOptionDTO =
        TimeSlotOptionDTO(
            id = this[TimeSlots.id].value.toString(),
            doctorId = this[Doctors.id].value.toString(),
            doctorFio = listOf(this[Doctors.surname], this[Doctors.name], this[Doctors.patronymic]).joinToString(" ").trim(),
            slotDate = this[TimeSlots.slotDate].toString(),
            startTime = this[TimeSlots.startTime].toString(),
            endTime = this[TimeSlots.endTime].toString()
        )
}
