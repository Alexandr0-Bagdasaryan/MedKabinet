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
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentType
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientEntity
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.pages.renderTableWithPagination
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindAppointmentsQuery
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindAvailableSlotsQuery

@Component
class AppointmentsPageHandler {

    fun renderPage(
        appointments: List<FindAppointmentsQuery.AppointmentRowDTO>,
        patients: List<PatientEntity>,
        slots: List<FindAvailableSlotsQuery.TimeSlotOptionDTO>,
        page: Int,
        hasPrev: Boolean,
        hasNext: Boolean,
        errorMessage: String?,
        doctorScope: Boolean = false
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
            h1("h3 mb-0") { +(if (doctorScope) "Мои записи" else "Записи к врачам") }
            span("text-muted") { +"Страница $page" }
        }

        div("card shadow-sm mb-4") {
            div("card-header") { +(if (doctorScope) "Новая запись к вам" else "Новая запись") }
            div("card-body") {
                if (!errorMessage.isNullOrBlank()) {
                    div("alert alert-danger mb-3") {
                        +errorMessage
                    }
                }

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
                                        value = p.id.value.toString()
                                        +"${p.fio.value} (${p.medicalCardNumber.value})"
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
                                        value = s.id.value.toString()
                                        +"${s.slotDate} ${s.startTime.value}-${s.endTime.value} | ${s.doctorFio.value}"
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
                            select {
                                id = "appointmentType"
                                name = "appointmentType"
                                classes = setOf("form-select")
                                required = true

                                option {
                                    value = ""
                                    +"Выберите тип приема"
                                }

                                AppointmentType.entries.forEach { type ->
                                    option {
                                        value = type.value
                                        +type.title
                                    }
                                }
                            }
                        }

                        div("col-12 col-md-6") {
                            label {
                                classes = setOf("form-label")
                                htmlFor = "appointmentStatus"
                                +"Статус"
                            }
                            select {
                                id = "appointmentStatus"
                                name = "appointmentStatus"
                                classes = setOf("form-select")
                                required = true

                                option {
                                    value = ""
                                    +"Выберите статус"
                                }

                                AppointmentStatus.entries.forEach { status ->
                                    option {
                                        value = status.value
                                        +status.title
                                    }
                                }
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
                td { +a.appointmentDate.toString() }
                td { +"${a.startTime.value} - ${a.endTime.value}" }
                td { +a.doctorFio.value }
                td { +a.patientFio.value }
                td { +a.appointmentType.title }
                td { +a.status.title }
                td { +(a.notes?.value ?: "-") }
            }
        }
    }
}
