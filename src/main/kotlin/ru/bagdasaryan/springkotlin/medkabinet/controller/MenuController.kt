package ru.bagdasaryan.springkotlin.medkabinet.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.AppointmentsPageHandler
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.DoctorsPageHandler
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.SchedulePageHandler
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindAppointmentsQuery
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindAvailableSlotsQuery
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.PatientRepository
import ru.bagdasaryan.springkotlin.medkabinet.service.AppointmentService

@RestController
class MenuController(
    private val schedulePageHandler: SchedulePageHandler,
    private val doctorsPageHandler: DoctorsPageHandler,
    private val appointmentsPageHandler: AppointmentsPageHandler,
    private val appointmentService: AppointmentService,
    private val findAppointmentsQuery: FindAppointmentsQuery,
    private val findAvailableSlotsQuery: FindAvailableSlotsQuery,
    private val patientRepository: PatientRepository
) {
    private val pageSize = 30

    @GetMapping("/schedule", produces = ["text/html; charset=UTF-8"])
    suspend fun schedulePage() = ResponseEntity.ok(schedulePageHandler.renderPage())

    @GetMapping("/doctors", produces = ["text/html; charset=UTF-8"])
    suspend fun doctorsPage(
        @RequestParam(name = "q", required = false) q: String?,
        @RequestParam(name = "page", required = false, defaultValue = "1") page: Int
    ) = ResponseEntity.ok(doctorsPageHandler.renderPage(q, page))

    @GetMapping("/doctors/search", produces = ["text/html; charset=UTF-8"])
    suspend fun doctorsSearch(@RequestParam(name = "q", required = false) q: String?) =
        ResponseEntity.ok(doctorsPageHandler.renderRows(q))

    @GetMapping("/appointments", produces = ["text/html; charset=UTF-8"])
    suspend fun appointmentsPage(
        @RequestParam(name = "page", required = false, defaultValue = "1") page: Int
    ): ResponseEntity<String> {
        val safePage = page.coerceAtLeast(1)

        val appointmentsFetched = findAppointmentsQuery.execute(safePage, pageSize).getOrThrow()
        val hasNext = appointmentsFetched.size > pageSize
        val appointments = appointmentsFetched.take(pageSize)

        val slots = findAvailableSlotsQuery.execute().getOrThrow()
        val patients = patientRepository.getAll().getOrThrow()

        return ResponseEntity.ok(
            appointmentsPageHandler.renderPage(
                appointments = appointments,
                patients = patients,
                slots = slots,
                page = safePage,
                hasPrev = safePage > 1,
                hasNext = hasNext
            )
        )
    }

    @PostMapping("/appointments")
    suspend fun createAppointment(
        @RequestParam patientId: Int,
        @RequestParam timeSlotId: Int,
        @RequestParam appointmentType: String,
        @RequestParam(required = false) notes: String?
    ): ResponseEntity<Void> {
        appointmentService.createAppointment(
            patientId = patientId,
            timeSlotId = timeSlotId,
            appointmentType = appointmentType,
            notes = notes
        ).getOrThrow()

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header(HttpHeaders.LOCATION, "/appointments")
            .build()
    }
}
