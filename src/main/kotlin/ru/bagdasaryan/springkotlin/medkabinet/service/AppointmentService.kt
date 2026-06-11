package ru.bagdasaryan.springkotlin.medkabinet.service

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentType
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.AppointmentPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.AppointmentPersister.CreateAppointmentDTO
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindAppointmentsQuery
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindAvailableSlotsQuery

@Service
class AppointmentService(
    private val appointmentPersister: AppointmentPersister,
    private val patientService: PatientService,
    private val timeSlotService: TimeSlotService,
    private val findAppointmentsQuery: FindAppointmentsQuery,
    private val findAvailableSlotsQuery: FindAvailableSlotsQuery
) {
    data class PageResult<T>(
        val rows: List<T>,
        val hasNext: Boolean
    )

    suspend fun findAppointments(
        page: Int,
        pageSize: Int,
        doctorId: DoctorId? = null
    ): Result<PageResult<FindAppointmentsQuery.AppointmentRowDTO>> =
        findAppointmentsQuery.execute(page, pageSize, doctorId).mapCatching { rows ->
            val hasNext = rows.size > pageSize
            PageResult(rows = rows.take(pageSize), hasNext = hasNext)
        }

    suspend fun findAvailableSlots(doctorId: DoctorId? = null): Result<List<FindAvailableSlotsQuery.TimeSlotOptionDTO>> =
        findAvailableSlotsQuery.execute(doctorId)

    suspend fun createAppointment(
        patientId: PatientId,
        timeSlotId: TimeSlotId,
        appointmentType: AppointmentType,
        appointmentStatus: AppointmentStatus,
        notes: AppointmentNote?,
        expectedDoctorId: DoctorId? = null
    ): Result<Unit> = runCatching {
        val patientExists = patientService.existsById(patientId).getOrThrow()
        require(patientExists) { "Пациент не найден" }

        val slot = timeSlotService.findAvailableById(timeSlotId).getOrThrow()
            ?: throw IllegalArgumentException("Выбранный слот недоступен")
        if (expectedDoctorId != null) {
            require(slot.doctorId == expectedDoctorId) { "Вы не можете создавать записи для другого врача" }
        }

        newSuspendedTransaction {
            appointmentPersister.createAppointment(
                CreateAppointmentDTO(
                    patientId = patientId,
                    timeSlot = slot,
                    appointmentType = appointmentType,
                    appointmentStatus = appointmentStatus,
                    notes = notes
                )
            ).getOrThrow()

            timeSlotService.markBooked(timeSlotId).getOrThrow()
        }
    }

}
