package ru.bagdasaryan.springkotlin.medkabinet.storage.persister

import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentType
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import java.time.LocalDate
import java.time.LocalTime

@Repository
interface PatientCsvTransferPersister {
    suspend fun importAppointments(
        patientId: PatientId,
        appointments: List<ImportedAppointmentRow>
    ): Result<Unit>

    data class ImportedAppointmentRow(
        val doctorId: DoctorId,
        val appointmentDate: LocalDate,
        val startTime: LocalTime,
        val endTime: LocalTime,
        val appointmentType: AppointmentType,
        val appointmentStatus: AppointmentStatus,
        val appointmentNote: AppointmentNote?
    )
}
