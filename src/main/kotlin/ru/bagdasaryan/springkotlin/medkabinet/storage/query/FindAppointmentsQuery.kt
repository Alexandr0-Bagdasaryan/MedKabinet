package ru.bagdasaryan.springkotlin.medkabinet.storage.query

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentId
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentType
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeOfDay
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Appointments
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Patients
import java.time.LocalDate

@Component
class FindAppointmentsQuery {

    suspend fun execute(page: Int, pageSize: Int, doctorId: DoctorId? = null): Result<List<AppointmentRowDTO>> = runCatching {
        val safePage = page.coerceAtLeast(1)
        val safeSize = pageSize.coerceAtLeast(1)
        val offset = (safePage - 1L) * safeSize

        newSuspendedTransaction {
            val query = Appointments
                .join(
                    Doctors,
                    JoinType.INNER,
                    Appointments.doctorId,
                    Doctors.id
                )
                .join(
                    Patients,
                    JoinType.INNER,
                    Patients.id,
                    Appointments.patientId
                )
                .selectAll()
            if (doctorId != null) {
                query.andWhere { Appointments.doctorId eq doctorId.value }
            }
            query
                .orderBy(Appointments.appointmentDate to SortOrder.DESC)
                .orderBy(Appointments.startTime to SortOrder.DESC)
                .limit(safeSize + 1)
                .offset(offset)
                .map { it.toDTO() }
        }
    }

    class AppointmentRowDTO(
        val appointmentId: AppointmentId,
        val appointmentDate: LocalDate,
        val startTime: TimeOfDay,
        val endTime: TimeOfDay,
        val doctorFio: Fio,
        val patientFio: Fio,
        val appointmentType: AppointmentType,
        val status: AppointmentStatus,
        val notes: AppointmentNote?
    )

    private fun ResultRow.toDTO(): AppointmentRowDTO =
        AppointmentRowDTO(
            appointmentId = AppointmentId(this[Appointments.id].value),
            appointmentDate = this[Appointments.appointmentDate],
            startTime = TimeOfDay.of(this[Appointments.startTime]),
            endTime = TimeOfDay.of(this[Appointments.endTime]),
            doctorFio = Fio.fromParts(this[Doctors.surname], this[Doctors.name], this[Doctors.patronymic]),
            patientFio = Fio.fromParts(this[Patients.surname], this[Patients.name], this[Patients.patronymic]),
            appointmentType = AppointmentType.from(this[Appointments.appointmentType]),
            status = AppointmentStatus.from(this[Appointments.status]),
            notes = this[Appointments.notes]?.let { AppointmentNote(it) }
        )
}
