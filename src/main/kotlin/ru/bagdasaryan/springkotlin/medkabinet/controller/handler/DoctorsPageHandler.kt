package ru.bagdasaryan.springkotlin.medkabinet.controller.handler

import kotlinx.html.InputType
import kotlinx.html.ThScope
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.unsafe
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.repository.DoctorRepository
import ru.bagdasaryan.springkotlin.medkabinet.service.DoctorService
import ru.bagdasaryan.springkotlin.medkabinet.vo.Fio

@Component
class DoctorsPageHandler(
    private val doctorService: DoctorService
) {
    suspend fun renderPage(q: String?): String {
        val doctors = findDoctors(q)
        return clinicDoctorsPage(doctors, q.orEmpty()) // private fun внутри этого класса
    }

    suspend fun renderRows(q: String?): String {
        val doctors = findDoctors(q)
        return renderDoctorsRows(doctors) // private fun внутри этого класса
    }

    private suspend fun findDoctors(q: String?): List<DoctorRepository.DoctorDTO> {
        if (q.isNullOrBlank()) return doctorService.findAll().getOrThrow()
        val fio = Fio.parse(q)
        return doctorService.findByFio(fio).getOrThrow()
    }

    private fun clinicDoctorsPage(
        doctors: List<DoctorRepository.DoctorDTO>,
        q: String = ""
    ): String = appLayout(
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
        div {
            classes = setOf("d-flex", "justify-content-between", "align-items-center", "mb-3")
            h1("h3 mb-0") { +"Врачи" }
            span("text-muted") { +"Всего врачей: ${doctors.size}" }
        }

        div("card shadow-sm") {
            div("card-body") {
                div("table-responsive") {
                    table("table table-striped table-hover align-middle mb-0") {
                        thead {
                            tr {
                                th { scope = ThScope.col; +"ФИО" }
                                th { scope = ThScope.col; +"Email" }
                                th { scope = ThScope.col; +"Телефон" }
                                th { scope = ThScope.col; +"Отделение" }
                                th { scope = ThScope.col; +"Лицензия до" }
                            }
                            tr {
                                th {
                                    input(InputType.search) {
                                        name = "q"
                                        value = q
                                        classes = setOf("form-control", "form-control-sm")
                                        placeholder = "Фильтр по ФИО"
                                        attributes["hx-get"] = "/doctors/search"
                                        attributes["hx-trigger"] = "keyup changed delay:300ms"
                                        attributes["hx-target"] = "#doctors-table-body"
                                        attributes["hx-swap"] = "innerHTML"
                                    }
                                }
                                th {}
                                th {}
                                th {}
                                th {}
                            }
                        }
                        tbody {
                            id = "doctors-table-body"
                            doctors.forEach { unsafe { +renderDoctorRow(it) } }
                        }
                    }
                }
            }
        }
    }

    private fun renderDoctorsRows(doctors: List<DoctorRepository.DoctorDTO>): String =
        doctors.joinToString(separator = "") { renderDoctorRow(it) }

    private fun renderDoctorRow(doctor: DoctorRepository.DoctorDTO): String = createHTML().tr {
        td { +listOf(doctor.lastName, doctor.firstName, doctor.middleName).joinToString(" ").trim() }
        td { +doctor.email }
        td { +doctor.phone }
        td { +doctor.departmentId }
        td { +doctor.licenseValidUntil }
    }

}
