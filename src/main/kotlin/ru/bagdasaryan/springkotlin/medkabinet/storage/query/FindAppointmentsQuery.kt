package ru.bagdasaryan.springkotlin.medkabinet.storage.query

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Appointments
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Patients

@Component
class FindAppointmentsQuery {

    suspend fun execute(): Result<List<AppointmentRowDTO>> = runCatching {
        newSuspendedTransaction {
            Appointments
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
                .orderBy(Appointments.appointmentDate to SortOrder.DESC)
                .orderBy(Appointments.startTime to SortOrder.DESC)
                .limit(200)
                .map { it.toDTO() }
        }
    }

    class AppointmentRowDTO(
        val appointmentId: String,
        val appointmentDate: String,
        val startTime: String,
        val endTime: String,
        val doctorFio: String,
        val patientFio: String,
        val appointmentType: String,
        val status: String,
        val notes: String?
    )

    private fun ResultRow.toDTO(): AppointmentRowDTO =
        AppointmentRowDTO(
            appointmentId = this[Appointments.id].value.toString(),
            appointmentDate = this[Appointments.appointmentDate].toString(),
            startTime = this[Appointments.startTime].toString(),
            endTime = this[Appointments.endTime].toString(),
            doctorFio = listOf(this[Doctors.surname], this[Doctors.name], this[Doctors.patronymic]).joinToString(" ").trim(),
            patientFio = listOf(this[Patients.surname], this[Patients.name], this[Patients.patronymic] ?: "").joinToString(" ").trim(),
            appointmentType = this[Appointments.appointmentType],
            status = this[Appointments.status],
            notes = this[Appointments.notes]
        )
}
