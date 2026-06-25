package ru.bagdasaryan.springkotlin.medkabinet.storage.query

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentId
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentType
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeOfDay
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Appointments
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors

@Component
class FindPatientAppointmentsForExportQuery {

    suspend fun findByPatientId(patientId: PatientId): Result<List<PatientAppointmentExportRowDTO>> = runCatching {
        newSuspendedTransaction {
            Appointments
                .join(Doctors, JoinType.INNER, Appointments.doctorId, Doctors.id)
                .selectAll()
                .where { Appointments.patientId eq patientId.value }
                .orderBy(Appointments.appointmentDate to SortOrder.ASC)
                .orderBy(Appointments.startTime to SortOrder.ASC)
                .map { it.toDTO() }
        }
    }

    class PatientAppointmentExportRowDTO(
        val appointmentId: AppointmentId,
        val doctorId: DoctorId,
        val doctorFio: Fio,
        val appointmentDate: java.time.LocalDate,
        val startTime: TimeOfDay,
        val endTime: TimeOfDay,
        val appointmentType: AppointmentType,
        val appointmentStatus: AppointmentStatus,
        val appointmentNote: AppointmentNote?
    )

    private fun ResultRow.toDTO(): PatientAppointmentExportRowDTO =
        PatientAppointmentExportRowDTO(
            appointmentId = AppointmentId(this[Appointments.id].value),
            doctorId = DoctorId(this[Appointments.doctorId]),
            doctorFio = Fio.fromParts(this[Doctors.surname], this[Doctors.name], this[Doctors.patronymic]),
            appointmentDate = this[Appointments.appointmentDate],
            startTime = TimeOfDay.of(this[Appointments.startTime]),
            endTime = TimeOfDay.of(this[Appointments.endTime]),
            appointmentType = AppointmentType.from(this[Appointments.appointmentType]),
            appointmentStatus = AppointmentStatus.from(this[Appointments.status]),
            appointmentNote = this[Appointments.notes]?.let(::AppointmentNote)
        )
}
