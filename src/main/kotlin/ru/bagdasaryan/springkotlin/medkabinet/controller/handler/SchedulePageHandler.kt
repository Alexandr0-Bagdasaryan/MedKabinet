package ru.bagdasaryan.springkotlin.medkabinet.controller.handler

import kotlinx.html.*
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.repository.DoctorRepository
import ru.bagdasaryan.springkotlin.medkabinet.service.DoctorService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Component
class SchedulePageHandler(
    private val doctorService: DoctorService
) {

    suspend fun renderPage(): String {
        val rows = doctorService.findAll().getOrThrow()
        val dateLabel = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru")))

        return clinicSchedulePage(
            dateLabel = dateLabel,
            rows = rows
        )
    }

    private enum class Status(val text: String, val badgeClasses: Set<String>) {
        AVAILABLE("Свободно", setOf("bg-success")),
        BOOKED("Занято", setOf("bg-secondary")),
        URGENT("Срочно", setOf("bg-danger")),
        BREAK("Перерыв", setOf("bg-warning", "text-dark"))
    }

    private fun clinicSchedulePage(
        dateLabel: String,
        rows: List<DoctorRepository.DoctorDTO>
    ): String = appLayout(
        pageTitle = "Расписание",
        brandHref = "/",
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
                        button {
                            type = ButtonType.submit
                            classes = setOf("btn", "btn-primary")
                            +"Применить"
                        }
                        a(href = "#") {
                            classes = setOf("btn", "btn-outline-secondary")
                            +"Сбросить"
                        }
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
                div("table-responsive") {
                    table("table table-striped table-hover align-middle mb-0") {
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

    private fun FlowContent.legendBadge(text: String, badgeClasses: Set<String>) {
        span {
            classes = setOf("badge", "rounded-pill") + badgeClasses
            +text
        }
    }
}

