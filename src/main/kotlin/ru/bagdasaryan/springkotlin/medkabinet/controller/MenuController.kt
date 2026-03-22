package ru.bagdasaryan.springkotlin.medkabinet.controller

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jetbrains.exposed.dao.id.IntIdTable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.repository.DoctorRepository
import ru.bagdasaryan.springkotlin.medkabinet.service.DoctorService
import ru.bagdasaryan.springkotlin.medkabinet.vo.Fio
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RestController
class MenuController(
    private val doctorService: DoctorService
) {
    @GetMapping("/schedule", produces = ["text/html; charset=UTF-8"])
    suspend fun schedulePage(): ResponseEntity<String> {
        val demoRows = doctorService.findAll().getOrThrow()

        val date = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))
        val dateLabel = date.format(formatter)

        val html = clinicSchedulePage(
            dateLabel = dateLabel,
            rows = demoRows
        )

        return ResponseEntity.ok(html)
    }

    @GetMapping("/doctors", produces = ["text/html; charset=UTF-8"])
    suspend fun doctorsPage(
        @RequestParam(name = "q", required = false) q: String?
    ): ResponseEntity<String> {
        val doctors = if (q.isNullOrBlank()) {
            doctorService.findAll().getOrThrow()
        } else {
            val fio = Fio.parse(q)
            doctorService.findByFio(fio).getOrElse { emptyList() }
        }

        return ResponseEntity.ok(clinicDoctorsPage(doctors, q.orEmpty()))
    }

    enum class Status(val text: String, val badgeClasses: Set<String>) {
        AVAILABLE("Свободно", setOf("bg-success")),
        BOOKED("Занято", setOf("bg-secondary")),
        URGENT("Срочно", setOf("bg-danger")),
        BREAK("Перерыв", setOf("bg-warning", "text-dark"))
    }

    fun clinicSchedulePage(
        dateLabel: String,
        rows: List<DoctorRepository.DoctorDTO>
    ): String = appLayout(
        pageTitle = "Расписание",
        brandHref = "/",
        logoUrl = "/img/logo.svg",
        nav = listOf(
            NavItem("Главная", "/"),
            NavItem("Расписание", "/schedule", active = true),
            NavItem("Пациенты", "/patients"),
            NavItem("Врачи", "/doctors")
        )
    ) {
        div {
            classes = setOf(
                "d-flex", "flex-wrap", "justify-content-between",
                "align-items-center", "gap-3", "mb-3"
            )
            div {
                h1 { classes = setOf("h3", "mb-1"); +"Расписание" }
                div { classes = setOf("text-muted"); +"Дата: $dateLabel" }
            }
            div {
                classes = setOf("d-flex", "flex-wrap", "gap-2")
                Status.entries.forEach { status ->
                    legendBadge(status.text, status.badgeClasses)
                }
            }
        }

        div {
            classes = setOf("card", "shadow-sm", "mb-4")
            div("card-body") {
                div {
                    classes = setOf("row", "g-3", "align-items-end")

                    div {
                        classes = setOf("col-12", "col-md-4")
                        label { classes = setOf("form-label"); htmlFor = "q"; +"Поиск (врач/специальность)" }
                        input(InputType.search) {
                            id = "q"; name = "q"
                            classes = setOf("form-control")
                            placeholder = "Например: кардиолог или Иванов"
                        }
                    }

                    div {
                        classes = setOf("col-12", "col-md-3")
                        label { classes = setOf("form-label"); htmlFor = "status"; +"Статус" }
                        select {
                            id = "status"; name = "status"
                            classes = setOf("form-select")
                            option { value = ""; +"Все" }
                            option { value = "AVAILABLE"; +"Свободно" }
                            option { value = "BOOKED"; +"Занято" }
                            option { value = "URGENT"; +"Срочно" }
                            option { value = "BREAK"; +"Перерыв" }
                        }
                    }

                    div {
                        classes = setOf("col-12", "col-md-5", "d-flex", "gap-2")
                        button { type = ButtonType.submit; classes = setOf("btn", "btn-primary"); +"Применить" }
                        a(href = "#") { classes = setOf("btn", "btn-outline-secondary"); +"Сбросить" }
                    }
                }
            }
        }

        div {
            classes = setOf("card", "shadow-sm")
            div("card-header") {
                classes = setOf("d-flex", "justify-content-between", "align-items-center")
                span(classes = "ms-2") { +"Приёмы" }
                span("text-muted me-2") { +"Всего слотов: ${rows.size}" }
            }
            div("card-body") {
                div {
                    classes = setOf("table-responsive")
                    table {
                        classes = setOf(
                            "table",
                            "table-striped",
                            "table-hover",
                            "align-middle",
                            "mb-0"
                        )
                        thead {
                            tr {
                                th { scope = ThScope.col; +"Время" }
                                th { scope = ThScope.col; +"Врач" }
                                th { scope = ThScope.col; +"Специальность" }
                                th { scope = ThScope.col; +"Каб." }
                                th { scope = ThScope.col; +"Статус" }
                                th { scope = ThScope.col; +"Комментарий" }
                                th { scope = ThScope.col; +"Действие" }
                            }
                        }
                        tbody {
                            rows.forEach { r ->
                                tr {
                                    td { +r.firstName }
                                    td { +r.lastName }
                                    td { +r.middleName }
                                    td { +r.departmentId }
                                    td { +r.email }
                                    td { +r.phone }
                                    td { +r.licenseValidUntil }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun FlowContent.legendBadge(text: String, badgeClasses: Set<String>) {
        span {
            classes = setOf("badge", "rounded-pill") + badgeClasses
            +text
        }
    }

    fun clinicDoctorsPage(doctors: List<DoctorRepository.DoctorDTO>, q: String = ""): String = appLayout(
        pageTitle = "Врачи",
        brandHref = "/",
        logoUrl = "/img/logo.svg",
        nav = listOf(
            NavItem("Главная", "/"),
            NavItem("Расписание", "/schedule"),
            NavItem("Пациенты", "/patients"),
            NavItem("Врачи", "/doctors", active = true)
        )
    ) {
        form(action = "/doctors", method = FormMethod.get) {
            classes = setOf("mb-3")
            div("input-group") {
                input(InputType.search, name = "q") {
                    classes = setOf("form-control")
                    placeholder = "Поиск по ФИО"
                    value = q
                }
                button(type = ButtonType.submit) {
                    classes = setOf("btn", "btn-primary")
                    +"Найти"
                }
            }
        }

        div {
            classes = setOf("d-flex", "justify-content-between", "align-items-center", "mb-3")
            h1("h3 mb-0") { +"Врачи" }
            span("text-muted") { +"Всего врачей: ${doctors.size}" }
        }

        div {
            classes = setOf("card", "shadow-sm")
            div("card-body") {
                div("table-responsive") {
                    table {
                        classes = setOf("table", "table-striped", "table-hover", "align-middle", "mb-0")
                        thead {
                            tr {
                                th { scope = ThScope.col; +"ФИО" }
                                th { scope = ThScope.col; +"Email" }
                                th { scope = ThScope.col; +"Телефон" }
                                th { scope = ThScope.col; +"Отделение" }
                                th { scope = ThScope.col; +"Лицензия до" }
                            }
                        }
                        tbody {
                            doctors.forEach { doctor ->
                                tr {
                                    td {
                                        +listOf(doctor.lastName, doctor.firstName, doctor.middleName).joinToString(" ")
                                            .trim()
                                    }
                                    td { +doctor.email }
                                    td { +doctor.phone }
                                    td { +"${doctor.departmentId}" }
                                    td { +"${doctor.licenseValidUntil}" }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}