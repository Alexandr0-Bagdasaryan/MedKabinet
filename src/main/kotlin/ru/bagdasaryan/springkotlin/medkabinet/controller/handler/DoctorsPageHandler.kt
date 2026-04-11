package ru.bagdasaryan.springkotlin.medkabinet.controller.handler

import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.input
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.td
import kotlinx.html.tr
import kotlinx.html.unsafe
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.pages.renderTableWithPagination
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.DoctorRepository
import ru.bagdasaryan.springkotlin.medkabinet.service.DoctorService
import ru.bagdasaryan.springkotlin.medkabinet.vo.Fio

@Component
class DoctorsPageHandler(
    private val doctorService: DoctorService
) {
    private val pageSize = 30

    suspend fun renderPage(q: String?, page: Int): String {
        val allDoctors = findDoctors(q)
        val safePage = page.coerceAtLeast(1)
        val fromIndex = (safePage - 1) * pageSize
        val toIndex = (fromIndex + pageSize).coerceAtMost(allDoctors.size)

        val paged = if (fromIndex >= allDoctors.size) emptyList() else allDoctors.subList(fromIndex, toIndex)
        val hasPrev = safePage > 1
        val hasNext = toIndex < allDoctors.size

        return clinicDoctorsPage(
            doctors = paged,
            q = q.orEmpty(),
            page = safePage,
            hasPrev = hasPrev,
            hasNext = hasNext,
            total = allDoctors.size
        )
    }

    suspend fun renderRows(q: String?): String {
        val doctors = findDoctors(q)
        return renderDoctorsRows(doctors)
    }

    private suspend fun findDoctors(q: String?): List<DoctorRepository.DoctorDTO> {
        if (q.isNullOrBlank()) return doctorService.findAll().getOrThrow()
        val fio = Fio.parse(q)
        return doctorService.findByFio(fio).getOrThrow()
    }

    private fun clinicDoctorsPage(
        doctors: List<DoctorRepository.DoctorDTO>,
        q: String,
        page: Int,
        hasPrev: Boolean,
        hasNext: Boolean,
        total: Int
    ): String = appLayout(
        pageTitle = "Врачи",
        brandHref = "/",
        nav = listOf(
            NavItem("Главная", "/"),
            NavItem("Расписание", "/schedule"),
            NavItem("Пациенты", "/patients"),
            NavItem("Врачи", "/doctors", active = true),
            NavItem("Записи", "/appointments")
        )
    ) {
        div {
            classes = setOf("d-flex", "justify-content-between", "align-items-center", "mb-3")
            h1("h3 mb-0") { +"Врачи" }
            span("text-muted") { +"Всего врачей: $total" }
        }

        div("card shadow-sm mb-3") {
            div("card-body") {
                form(action = "/doctors", method = FormMethod.get) {
                    classes = setOf("row", "g-2", "mb-0")
                    div("col-12 col-md-6") {
                        input(InputType.search) {
                            name = "q"
                            value = q
                            classes = setOf("form-control")
                            placeholder = "Фильтр по ФИО"
                            attributes["hx-get"] = "/doctors/search"
                            attributes["hx-trigger"] = "keyup changed delay:300ms"
                            attributes["hx-target"] = "#doctors-table-body"
                            attributes["hx-swap"] = "innerHTML"
                        }
                    }
                    div("col-auto") {
                        button(type = ButtonType.submit) {
                            classes = setOf("btn", "btn-primary")
                            +"Найти"
                        }
                    }
                    if (q.isNotBlank()) {
                        div("col-auto") {
                            a(href = "/doctors", classes = "btn btn-outline-secondary") { +"Сбросить" }
                        }
                    }
                }
            }
        }

        val prevHref = if (q.isBlank()) "/doctors?page=${page - 1}" else "/doctors?page=${page - 1}&q=$q"
        val nextHref = if (q.isBlank()) "/doctors?page=${page + 1}" else "/doctors?page=${page + 1}&q=$q"

        renderTableWithPagination(
            cardTitle = "Список врачей",
            headers = listOf("ФИО", "Email", "Телефон", "Отделение", "Лицензия до"),
            rows = doctors,
            page = page,
            hasPrev = hasPrev,
            hasNext = hasNext,
            prevHref = prevHref,
            nextHref = nextHref,
            emptyMessage = "Врачи не найдены",
            tbodyId = "doctors-table-body"
        ) { doctor ->
            tr {
                td { +listOf(doctor.lastName, doctor.firstName, doctor.middleName).joinToString(" ").trim() }
                td { +doctor.email }
                td { +doctor.phone }
                td { +doctor.departmentId }
                td { +doctor.licenseValidUntil }
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
