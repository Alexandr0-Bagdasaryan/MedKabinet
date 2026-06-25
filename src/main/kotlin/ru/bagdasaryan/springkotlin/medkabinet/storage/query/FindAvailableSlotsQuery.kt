package ru.bagdasaryan.springkotlin.medkabinet.storage.query

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId
import ru.bagdasaryan.springkotlin.medkabinet.domain.ScheduleId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeOfDay
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeSlotStatus
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.TimeSlots
import java.time.LocalDate

@Component
class FindAvailableSlotsQuery {

    suspend fun execute(doctorId: DoctorId? = null): Result<List<TimeSlotOptionDTO>> = runCatching {
        newSuspendedTransaction {
            val query = TimeSlots
                .join(
                    Doctors,
                    JoinType.INNER,
                    TimeSlots.doctorId,
                    Doctors.id
                )
                .selectAll()
                .where { TimeSlots.status eq TimeSlotStatus.AVAILABLE.value }
            if (doctorId != null) {
                query.andWhere { TimeSlots.doctorId eq doctorId.value }
            }
            query
                .orderBy(TimeSlots.slotDate to SortOrder.ASC)
                .orderBy(TimeSlots.startTime to SortOrder.ASC)
                .limit(1000)
                .map { it.toDTO() }
        }
    }

    class TimeSlotOptionDTO(
        val id: TimeSlotId,
        val scheduleId: ScheduleId,
        val doctorId: DoctorId,
        val doctorFio: Fio,
        val slotDate: LocalDate,
        val startTime: TimeOfDay,
        val endTime: TimeOfDay
    )

    private fun ResultRow.toDTO(): TimeSlotOptionDTO =
        TimeSlotOptionDTO(
            id = TimeSlotId(this[TimeSlots.id].value),
            scheduleId = ScheduleId(this[TimeSlots.scheduleId]),
            doctorId = DoctorId(this[Doctors.id].value),
            doctorFio = Fio.fromParts(this[Doctors.surname], this[Doctors.name], this[Doctors.patronymic]),
            slotDate = this[TimeSlots.slotDate],
            startTime = TimeOfDay.of(this[TimeSlots.startTime]),
            endTime = TimeOfDay.of(this[TimeSlots.endTime])
        )
}
