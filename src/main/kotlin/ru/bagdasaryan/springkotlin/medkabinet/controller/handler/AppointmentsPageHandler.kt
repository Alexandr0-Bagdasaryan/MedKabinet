package ru.bagdasaryan.springkotlin.medkabinet.controller.handler

import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.ThScope
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
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.textArea
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindAppointmentsQuery
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindAvailableSlotsQuery
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.PatientRepository

@Component
class AppointmentsPageHandler {

    fun renderPage(
        appointments: List<FindAppointmentsQuery.AppointmentRowDTO>,
        patients: List<PatientRepository.PatientDTO>,
        slots: List<FindAvailableSlotsQuery.TimeSlotOptionDTO>
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
            span("text-muted") { +"Всего записей: ${appointments.size}" }
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

        div("card shadow-sm") {
            div("card-header") { +"Список записей" }
            div("card-body") {
                div("table-responsive") {
                    table("table table-striped table-hover align-middle mb-0") {
                        thead {
                            tr {
                                th { scope = ThScope.col; +"Дата" }
                                th { scope = ThScope.col; +"Время" }
                                th { scope = ThScope.col; +"Врач" }
                                th { scope = ThScope.col; +"Пациент" }
                                th { scope = ThScope.col; +"Тип" }
                                th { scope = ThScope.col; +"Статус" }
                                th { scope = ThScope.col; +"Примечание" }
                            }
                        }
                        tbody {
                            appointments.forEach { a ->
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
                }
            }
        }
    }
}
