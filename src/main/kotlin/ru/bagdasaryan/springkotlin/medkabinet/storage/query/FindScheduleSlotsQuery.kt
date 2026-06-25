package ru.bagdasaryan.springkotlin.medkabinet.storage.query

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentName
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentRoomNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.ScheduleId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationName
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeOfDay
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeSlotStatus
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Departments
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Specializations
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.TimeSlots
import java.time.LocalDate

@Component
class FindScheduleSlotsQuery {
    suspend fun execute(
        date: LocalDate,
        doctorId: DoctorId? = null
    ): Result<List<ScheduleSlotRowDTO>> = runCatching {
        newSuspendedTransaction {
            val rows = TimeSlots
                .join(Doctors, JoinType.INNER, TimeSlots.doctorId, Doctors.id)
                .join(Departments, JoinType.INNER, Doctors.departmentId, Departments.id)
                .join(Specializations, JoinType.INNER, Doctors.specializationId, Specializations.id)
                .selectAll()
                .where { TimeSlots.slotDate eq date }
                .orderBy(TimeSlots.startTime to SortOrder.ASC)
                .map { it.toDTO() }

            if (doctorId == null) rows else rows.filter { it.doctorId == doctorId }
        }
    }

    class ScheduleSlotRowDTO(
        val id: TimeSlotId,
        val scheduleId: ScheduleId,
        val doctorId: DoctorId,
        val doctorFio: Fio,
        val specializationName: SpecializationName,
        val departmentId: DepartmentId,
        val departmentName: DepartmentName,
        val departmentRoomNumber: DepartmentRoomNumber?,
        val slotDate: LocalDate,
        val startTime: TimeOfDay,
        val endTime: TimeOfDay,
        val status: TimeSlotStatus
    )

    private fun ResultRow.toDTO(): ScheduleSlotRowDTO =
        ScheduleSlotRowDTO(
            id = TimeSlotId(this[TimeSlots.id].value),
            scheduleId = ScheduleId(this[TimeSlots.scheduleId]),
            doctorId = DoctorId(this[Doctors.id].value),
            doctorFio = Fio.fromParts(
                this[Doctors.surname],
                this[Doctors.name],
                this[Doctors.patronymic]
            ),
            specializationName = SpecializationName(this[Specializations.name]),
            departmentId = DepartmentId(this[Departments.id].value),
            departmentName = DepartmentName(this[Departments.name]),
            departmentRoomNumber = this[Departments.roomNumber]?.let { DepartmentRoomNumber(it) },
            slotDate = this[TimeSlots.slotDate],
            startTime = TimeOfDay.of(this[TimeSlots.startTime]),
            endTime = TimeOfDay.of(this[TimeSlots.endTime]),
            status = TimeSlotStatus.from(this[TimeSlots.status])
        )
}
