package ru.bagdasaryan.springkotlin.medkabinet.storage.query

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Appointments
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Departments
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors
import java.time.LocalDate

@Component
class AppointmentLoadQuery {
    suspend fun execute(fromDate: LocalDate, toDate: LocalDate, doctorId: DoctorId? = null): Result<AppointmentLoadDTO> = runCatching {
        newSuspendedTransaction {
            val dateCondition = Appointments.appointmentDate.between(fromDate, toDate)
            val query = Appointments
                .join(Doctors, JoinType.INNER, Appointments.doctorId, Doctors.id)
                .join(Departments, JoinType.INNER, Doctors.departmentId, Departments.id)
                .selectAll()
                .where { dateCondition }
            if (doctorId != null) {
                query.andWhere { Appointments.doctorId eq doctorId.value }
            }
            val rows = query
                .map { it.toLoadRow() }

            val doctorLoads = rows
                .groupBy { it.doctorId }
                .map { (doctorId, doctorRows) ->
                    DoctorLoadDTO(
                        doctorId = doctorId,
                        doctorFio = doctorRows.first().doctorFio,
                        totalAppointments = doctorRows.size.toLong()
                    )
                }
                .sortedByDescending { it.totalAppointments }

            val departmentLoads = rows
                .groupBy { it.departmentId }
                .map { (departmentId, departmentRows) ->
                    DepartmentLoadDTO(
                        departmentId = departmentId,
                        departmentName = departmentRows.first().departmentName,
                        totalAppointments = departmentRows.size.toLong()
                    )
                }
                .sortedByDescending { it.totalAppointments }

            AppointmentLoadDTO(
                fromDate = fromDate,
                toDate = toDate,
                totalAppointments = rows.size.toLong(),
                doctorLoads = doctorLoads,
                departmentLoads = departmentLoads
            )
        }
    }

    private data class AppointmentLoadRowDTO(
        val doctorId: DoctorId,
        val doctorFio: Fio,
        val departmentId: DepartmentId,
        val departmentName: String
    )

    class AppointmentLoadDTO(
        val fromDate: LocalDate,
        val toDate: LocalDate,
        val totalAppointments: Long,
        val doctorLoads: List<DoctorLoadDTO>,
        val departmentLoads: List<DepartmentLoadDTO>
    )

    class DoctorLoadDTO(
        val doctorId: DoctorId,
        val doctorFio: Fio,
        val totalAppointments: Long
    )

    class DepartmentLoadDTO(
        val departmentId: DepartmentId,
        val departmentName: String,
        val totalAppointments: Long
    )

    private fun ResultRow.toLoadRow(): AppointmentLoadRowDTO =
        AppointmentLoadRowDTO(
            doctorId = DoctorId(this[Doctors.id].value),
            doctorFio = Fio.fromParts(this[Doctors.surname], this[Doctors.name], this[Doctors.patronymic]),
            departmentId = DepartmentId(this[Departments.id].value),
            departmentName = this[Departments.name]
        )
}
