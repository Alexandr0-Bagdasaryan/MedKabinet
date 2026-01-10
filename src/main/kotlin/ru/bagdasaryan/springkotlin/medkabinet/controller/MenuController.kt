package ru.bagdasaryan.springkotlin.medkabinet.controller

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RestController
class MenuController {
    @GetMapping("/schedule", produces = ["text/html; charset=UTF-8"])
    fun schedulePage() : ResponseEntity<String>{
        // test Data
        val demoRows = listOf(
            AppointmentRow("09:00", "Иванов А.А.", "Терапевт", "101", Status.AVAILABLE),
            AppointmentRow("09:30", "Петрова И.В.", "Кардиолог", "203", Status.BOOKED, "Повторный приём"),
            AppointmentRow("10:00", "Сидоров М.Н.", "Невролог", "305", Status.URGENT, "Окно для экстренных"),
            AppointmentRow("10:30", "—", "—", "—", Status.BREAK, "Технический перерыв"),
            AppointmentRow("11:00", "Кузнецова Е.С.", "Эндокринолог", "210", Status.AVAILABLE)
        )

        val date = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))
        val dateLabel = date.format(formatter)

        val html = clinicSchedulePage(
            dateLabel = dateLabel,
            rows = demoRows
        )

        return ResponseEntity.ok(html)
    }

    data class AppointmentRow(
        val time: String,
        val doctor: String,
        val specialty: String,
        val room: String,
        val status: Status,
        val comment: String? = null
    )

    enum class Status(val text: String, val badgeClasses: Set<String>) {
        AVAILABLE("Свободно", setOf("bg-success")),
        BOOKED("Занято", setOf("bg-secondary")),
        URGENT("Срочно", setOf("bg-danger")),
        BREAK("Перерыв", setOf("bg-warning", "text-dark"))
    }

    fun clinicSchedulePage(
        dateLabel: String,
        rows: List<AppointmentRow>
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
        // ВНУТРИ appLayout рисуем только содержимое main-контейнера

        // Header + legend
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
                legendBadge("Свободно", setOf("bg-success"))
                legendBadge("Занято", setOf("bg-secondary"))
                legendBadge("Срочно", setOf("bg-danger"))
                legendBadge("Перерыв", setOf("bg-warning", "text-dark"))
            }
        }

        // Filter card (UI only)
        div {
            classes = setOf("card", "shadow-sm", "mb-4")
            div("card-body") {
                div { classes = setOf("row", "g-3", "align-items-end")

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

        // Table card
        div {
            classes = setOf("card", "shadow-sm")
            div("card-header") {
                classes = setOf("d-flex", "justify-content-between", "align-items-center")
                span(classes = "ms-2") { +"Приёмы" }
                span("text-muted me-2") { +"Всего слотов: ${rows.size}" }
            }
            div("card-body") {
                div {
                    classes = setOf("table-responsive") // wrapper for responsive tables [web:72]
                    table {
                        classes = setOf("table", "table-striped", "table-hover", "align-middle", "mb-0") // Bootstrap table classes [web:71]
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
                                    td { +r.time }
                                    td { +r.doctor }
                                    td { +r.specialty }
                                    td { +r.room }
                                    td {
                                        span {
                                            classes = setOf("badge", "rounded-pill") + r.status.badgeClasses
                                            +r.status.text
                                        }
                                    }
                                    td { +(r.comment ?: "—") }
                                    td {
                                        when (r.status) {
                                            Status.AVAILABLE -> button {
                                                classes = setOf("btn", "btn-sm", "btn-success")
                                                +"Записаться"
                                            }
                                            Status.BOOKED -> button {
                                                classes = setOf("btn", "btn-sm", "btn-outline-secondary")
                                                disabled = true
                                                +"Недоступно"
                                            }
                                            Status.URGENT -> button {
                                                classes = setOf("btn", "btn-sm", "btn-danger")
                                                +"Уточнить"
                                            }
                                            Status.BREAK -> span("text-muted") { +"—" }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun FlowContent.legendBadge(text: String, badgeClasses: Set<String>) {
        span {
            classes = setOf("badge", "rounded-pill") + badgeClasses
            +text
        }
    }
}