package ru.bagdasaryan.springkotlin.medkabinet.controller.handler

import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.stream.createHTML
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.entity.DoctorEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.TimeSlotStatus
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.service.DoctorService
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindScheduleSlotsQuery
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Component
class SchedulePageHandler(
    private val doctorService: DoctorService,
    private val findScheduleSlotsQuery: FindScheduleSlotsQuery
) {
    suspend fun renderPage(date: LocalDate? = null, doctorId: Int? = null, scopeDoctorId: DoctorId? = null): String {
        val data = loadScheduleData(date, doctorId, scopeDoctorId)
        return appLayout(
            pageTitle = "Расписание",
            brandHref = "/",
            nav = listOf(
                NavItem("Главная", "/"),
                NavItem("Расписание", "/schedule", active = true),
                NavItem("Пациенты", "/patients"),
                NavItem("Врачи", "/doctors"),
                NavItem("Записи", "/appointments")
            )
        ) {
            div {
                id = "schedule-content"
                renderScheduleContent(data)
            }
        }
    }

    suspend fun renderFragment(date: LocalDate? = null, doctorId: Int? = null, scopeDoctorId: DoctorId? = null): String {
        val data = loadScheduleData(date, doctorId, scopeDoctorId)
        return createHTML().div {
            id = "schedule-content"
            renderScheduleContent(data)
        }
    }

    private suspend fun loadScheduleData(date: LocalDate?, doctorId: Int?, scopeDoctorId: DoctorId?): SchedulePageData {
        val selectedDate = date ?: LocalDate.now()
        val selectedDoctorId = scopeDoctorId ?: doctorId?.let(::DoctorId)
        val doctors = if (scopeDoctorId != null) {
            listOfNotNull(doctorService.findById(scopeDoctorId).getOrThrow())
        } else {
            doctorService.findAll().getOrThrow()
        }
        val slots = findScheduleSlotsQuery.execute(selectedDate, selectedDoctorId).getOrThrow()

        val total = slots.size
        val free = slots.count { it.status == TimeSlotStatus.AVAILABLE }
        val booked = slots.count { it.status == TimeSlotStatus.BOOKED }
        val blocked = slots.count { it.status == TimeSlotStatus.BLOCKED }

        val doctorLoads = slots
            .groupBy { it.doctorId }
            .map { (id, doctorSlots) ->
                val doctor = doctorSlots.first().doctorFio
                DoctorLoad(doctorId = id, doctorFio = doctor, total = doctorSlots.size)
            }
            .sortedByDescending { it.total }

        val departmentLoads = slots
            .groupBy { it.departmentId }
            .map { (_, departmentSlots) ->
                DepartmentLoad(
                    departmentName = departmentSlots.first().departmentName,
                    total = departmentSlots.size
                )
            }
            .sortedByDescending { it.total }

        return SchedulePageData(
            selectedDate = selectedDate,
            selectedDoctorId = selectedDoctorId,
            doctors = doctors,
            slots = slots,
            total = total,
            free = free,
            booked = booked,
            blocked = blocked,
            doctorLoads = doctorLoads,
            departmentLoads = departmentLoads,
            doctorScope = scopeDoctorId != null
        )
    }

    private fun FlowContent.renderScheduleContent(data: SchedulePageData) {
        val dateLabel = data.selectedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru")))
        val selectedDoctor = data.doctors.firstOrNull { it.id == data.selectedDoctorId }
        div("mb-4") {
            div("d-flex flex-wrap justify-content-between align-items-start gap-3 mb-3") {
                div {
                    h1("h3 mb-1") { +"Расписание" }
                    div("text-muted") {
                        +"Рабочий день на $dateLabel"
                        if (selectedDoctor != null) {
                            +" · врач: ${selectedDoctor.fio.value}"
                        }
                    }
                }
                div("d-flex flex-wrap gap-2") {
                    dayLink("Вчера", data.selectedDate.minusDays(1), data.selectedDoctorId)
                    dayLink("Сегодня", LocalDate.now(), data.selectedDoctorId)
                    dayLink("Завтра", data.selectedDate.plusDays(1), data.selectedDoctorId)
                }
            }

            div("row g-3 mb-4") {
                statCard("Всего слотов", data.total.toString(), "На выбранную дату")
                statCard("Свободно", data.free.toString(), "Доступно для записи")
                statCard("Занято", data.booked.toString(), "Уже записаны пациенты")
                statCard("Заблокировано", data.blocked.toString(), "Недоступны для записи")
            }
        }

        div("card shadow-sm mb-4") {
            div("card-body") {
                form(method = FormMethod.get, action = "/schedule") {
                    id = "schedule-form"
                    div("row g-3 align-items-end") {
                        div("col-12 col-md-4") {
                            label("form-label") { +"Дата" }
                            input(InputType.date) {
                                id = "schedule-date"
                                name = "date"
                                value = data.selectedDate.toString()
                                classes = setOf("form-control")
                                attributes["hx-get"] = "/schedule/fragment"
                                attributes["hx-trigger"] = "change"
                                attributes["hx-target"] = "#schedule-content"
                                attributes["hx-swap"] = "outerHTML"
                                attributes["hx-include"] = "#schedule-form"
                            }
                        }
                        div("col-12 col-md-5") {
                            label("form-label") { +"Врач" }
                            select {
                                id = "schedule-doctor"
                                name = "doctorId"
                                classes = setOf("form-select")
                                if (data.doctorScope) {
                                    disabled = true
                                }
                                attributes["hx-get"] = "/schedule/fragment"
                                attributes["hx-trigger"] = "change"
                                attributes["hx-target"] = "#schedule-content"
                                attributes["hx-swap"] = "outerHTML"
                                attributes["hx-include"] = "#schedule-form"
                                option {
                                    value = ""
                                    +"Все врачи"
                                }
                                data.doctors.forEach { doctor ->
                                    option {
                                        value = doctor.id.value.toString()
                                        if (doctor.id == data.selectedDoctorId) {
                                            selected = true
                                        }
                                        +doctor.fio.value
                                    }
                                }
                            }
                            if (data.doctorScope && data.selectedDoctorId != null) {
                                input(InputType.hidden) {
                                    name = "doctorId"
                                    value = data.selectedDoctorId.value.toString()
                                }
                            }
                        }
                        div("col-12 col-md-3") {
                            a(href = "/schedule", classes = "btn btn-outline-secondary w-100") {
                                +"Сбросить"
                            }
                        }
                    }
                }
            }
        }

        div("row g-4") {
            div("col-12 col-xl-8") {
                div("card shadow-sm h-100") {
                    div("card-header d-flex justify-content-between align-items-center flex-wrap gap-2") {
                        span { +"Слоты на день" }
                        span("badge rounded-pill bg-primary") { +"${data.slots.size} записей" }
                    }
                    div("card-body") {
                        div("table-responsive") {
                            table("table table-striped table-hover align-middle mb-0") {
                                thead {
                                    tr {
                                        th { +"Время" }
                                        th { +"Врач" }
                                        th { +"Отделение" }
                                        th { +"Кабинет" }
                                        th { +"Статус" }
                                    }
                                }
                                tbody {
                                    if (data.slots.isEmpty()) {
                                        tr {
                                            td {
                                                colSpan = "5"
                                                classes = setOf("text-center", "text-muted")
                                                +"На выбранную дату слоты не найдены"
                                            }
                                        }
                                    } else {
                                        data.slots.forEach { slot ->
                                            tr {
                                                td { +slotTimeRange(slot) }
                                                td {
                                                    div("fw-semibold") { +slot.doctorFio.value }
                                                    div("text-muted small") { +slot.specializationName.value }
                                                }
                                                td { +slot.departmentName.value }
                                                td { +(slot.departmentRoomNumber?.value ?: "-") }
                                                td { slotStatusBadge(slot.status) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            div("col-12 col-xl-4") {
                div("d-grid gap-3") {
                    infoCard(
                        title = "Самый загруженный врач",
                        value = data.doctorLoads.firstOrNull()?.doctorFio?.value ?: "-",
                        subtitle = data.doctorLoads.firstOrNull()?.total?.let { "$it слотов" } ?: "Нет данных"
                    )
                    infoCard(
                        title = "Самое загруженное отделение",
                        value = data.departmentLoads.firstOrNull()?.departmentName?.value ?: "-",
                        subtitle = data.departmentLoads.firstOrNull()?.total?.let { "$it слотов" } ?: "Нет данных"
                    )
                    loadListCard(
                        title = "Нагрузка по врачам",
                        rows = data.doctorLoads.map { "${it.doctorFio.value} — ${it.total}" }
                    )
                }
            }
        }

        div("mt-4") {
            loadListCard(
                title = "Нагрузка по отделениям",
                rows = data.departmentLoads.map { "${it.departmentName.value} — ${it.total}" }
            )
        }
    }

    private fun FlowContent.dayLink(label: String, date: LocalDate, doctorId: DoctorId?) {
        val href = buildString {
            append("/schedule?date=")
            append(date)
            if (doctorId != null) {
                append("&doctorId=")
                append(doctorId.value)
            }
        }
        a(href = href, classes = "btn btn-outline-primary") { +label }
    }

    private fun FlowContent.statCard(title: String, value: String, subtitle: String) {
        div("col-12 col-md-6 col-xl-3") {
            div("card shadow-sm h-100") {
                div("card-body") {
                    div("text-muted small mb-1") { +title }
                    h2("h4 mb-1") { +value }
                    div("text-muted small") { +subtitle }
                }
            }
        }
    }

    private fun FlowContent.infoCard(title: String, value: String, subtitle: String) {
        div("card shadow-sm") {
            div("card-body") {
                div("text-muted small mb-1") { +title }
                h3("h5 mb-1") { +value }
                div("text-muted small") { +subtitle }
            }
        }
    }

    private fun FlowContent.loadListCard(title: String, rows: List<String>) {
        div("card shadow-sm") {
            div("card-body") {
                h3("h6 mb-3") { +title }
                if (rows.isEmpty()) {
                    div("text-muted small") { +"Нет данных" }
                } else {
                    rows.forEachIndexed { index, row ->
                        div("d-flex justify-content-between align-items-center py-1") {
                            span("text-body") { +(index + 1).toString() }
                            span("ms-3 flex-grow-1") { +row }
                        }
                    }
                }
            }
        }
    }

    private fun FlowContent.slotStatusBadge(status: TimeSlotStatus) {
        val classes = when (status) {
            TimeSlotStatus.AVAILABLE -> setOf("badge", "rounded-pill", "bg-success")
            TimeSlotStatus.BOOKED -> setOf("badge", "rounded-pill", "bg-secondary")
            TimeSlotStatus.BLOCKED -> setOf("badge", "rounded-pill", "bg-danger")
        }
        span {
            this.classes = classes
            +status.title
        }
    }

    private fun slotTimeRange(slot: FindScheduleSlotsQuery.ScheduleSlotRowDTO): String =
        "${slot.startTime.value} - ${slot.endTime.value}"

    private data class DoctorLoad(
        val doctorId: DoctorId,
        val doctorFio: ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio,
        val total: Int
    )

    private data class DepartmentLoad(
        val departmentName: ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentName,
        val total: Int
    )

    private data class SchedulePageData(
        val selectedDate: LocalDate,
        val selectedDoctorId: DoctorId?,
        val doctors: List<DoctorEntity>,
        val slots: List<FindScheduleSlotsQuery.ScheduleSlotRowDTO>,
        val total: Int,
        val free: Int,
        val booked: Int,
        val blocked: Int,
        val doctorLoads: List<DoctorLoad>,
        val departmentLoads: List<DepartmentLoad>,
        val doctorScope: Boolean
    )
}
