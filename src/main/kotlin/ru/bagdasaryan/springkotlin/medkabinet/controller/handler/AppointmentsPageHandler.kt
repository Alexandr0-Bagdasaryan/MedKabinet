package ru.bagdasaryan.springkotlin.medkabinet.controller.handler

import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.span
import kotlinx.html.td
import kotlinx.html.textArea
import kotlinx.html.tr
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.pages.renderTableWithPagination
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindAppointmentsQuery
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindAvailableSlotsQuery
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.PatientRepository

@Component
class AppointmentsPageHandler {

    fun renderPage(
        appointments: List<FindAppointmentsQuery.AppointmentRowDTO>,
        patients: List<PatientRepository.PatientDTO>,
        slots: List<FindAvailableSlotsQuery.TimeSlotOptionDTO>,
        page: Int,
        hasPrev: Boolean,
        hasNext: Boolean
    ): String = appLayout(
        pageTitle = "Записи",
        brandHref = "/",
        nav = listOf(
            NavItem("Главная", "/"),
            NavItem("Расписание", "/schedule"),
            NavItem("Пациенты", "/patients"),
            NavItem("Врачи", "/doctors"),
            NavItem("Записи", "/appointments", active = true)
        )
    ) {
        div {
            classes = setOf("d-flex", "justify-content-between", "align-items-center", "mb-3")
            h1("h3 mb-0") { +"Записи к врачам" }
            span("text-muted") { +"Страница $page" }
        }

        div("card shadow-sm mb-4") {
            div("card-header") { +"Новая запись" }
            div("card-body") {
                form(action = "/appointments", method = FormMethod.post) {
                    div("row g-3") {
                        div("col-12 col-md-6") {
                            label {
                                classes = setOf("form-label")
                                htmlFor = "patientId"
                                +"Пациент"
                            }
                            select {
                                id = "patientId"
                                name = "patientId"
                                classes = setOf("form-select")
                                required = true

                                option {
                                    value = ""
                                    +"Выберите пациента"
                                }

                                patients.forEach { p ->
                                    option {
                                        value = p.id
                                        +"${p.fio} (${p.cardNumber})"
                                    }
                                }
                            }
                        }

                        div("col-12 col-md-6") {
                            label {
                                classes = setOf("form-label")
                                htmlFor = "timeSlotId"
                                +"Слот"
                            }
                            select {
                                id = "timeSlotId"
                                name = "timeSlotId"
                                classes = setOf("form-select")
                                required = true

                                option {
                                    value = ""
                                    +"Выберите свободный слот"
                                }

                                slots.forEach { s ->
                                    option {
                                        value = s.id
                                        +"${s.slotDate} ${s.startTime}-${s.endTime} | ${s.doctorFio}"
                                    }
                                }
                            }
                        }

                        div("col-12 col-md-6") {
                            label {
                                classes = setOf("form-label")
                                htmlFor = "appointmentType"
                                +"Тип приема"
                            }
                            input(InputType.text) {
                                id = "appointmentType"
                                name = "appointmentType"
                                classes = setOf("form-control")
                                placeholder = "Первичный прием"
                                required = true
                            }
                        }

                        div("col-12") {
                            label {
                                classes = setOf("form-label")
                                htmlFor = "notes"
                                +"Примечание"
                            }
                            textArea(rows = "3") {
                                id = "notes"
                                name = "notes"
                                classes = setOf("form-control")
                                placeholder = "Дополнительная информация"
                            }
                        }

                        div("col-12") {
                            button(type = ButtonType.submit) {
                                classes = setOf("btn", "btn-primary")
                                +"Создать запись"
                            }
                        }
                    }
                }
            }
        }

        renderTableWithPagination(
            cardTitle = "Список записей",
            headers = listOf("Дата", "Время", "Врач", "Пациент", "Тип", "Статус", "Примечание"),
            rows = appointments,
            page = page,
            hasPrev = hasPrev,
            hasNext = hasNext,
            prevHref = "/appointments?page=${page - 1}",
            nextHref = "/appointments?page=${page + 1}",
            emptyMessage = "Записей пока нет"
        ) { a ->
            tr {
                td { +a.appointmentDate }
                td { +"${a.startTime} - ${a.endTime}" }
                td { +a.doctorFio }
                td { +a.patientFio }
                td { +a.appointmentType }
                td { +a.status }
                td { +(a.notes ?: "-") }
            }
        }
    }
}
