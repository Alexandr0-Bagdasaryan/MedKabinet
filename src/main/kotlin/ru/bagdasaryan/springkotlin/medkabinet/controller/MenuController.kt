package ru.bagdasaryan.springkotlin.medkabinet.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.DoctorsPageHandler
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.SchedulePageHandler

@RestController
class MenuController(
    private val schedulePageHandler: SchedulePageHandler,
    private val doctorsPageHandler: DoctorsPageHandler
) {
    @GetMapping("/schedule", produces = ["text/html; charset=UTF-8"])
    suspend fun schedulePage() = ResponseEntity.ok(schedulePageHandler.renderPage())

    @GetMapping("/doctors", produces = ["text/html; charset=UTF-8"])
    suspend fun doctorsPage(@RequestParam(name = "q", required = false) q: String?) =
        ResponseEntity.ok(doctorsPageHandler.renderPage(q))

    @GetMapping("/doctors/search", produces = ["text/html; charset=UTF-8"])
    suspend fun doctorsSearch(@RequestParam(name = "q", required = false) q: String?) =
        ResponseEntity.ok(doctorsPageHandler.renderRows(q))
}
