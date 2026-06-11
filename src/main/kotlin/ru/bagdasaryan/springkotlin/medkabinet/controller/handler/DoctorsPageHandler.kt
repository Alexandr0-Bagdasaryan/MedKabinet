package ru.bagdasaryan.springkotlin.medkabinet.controller.handler

import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
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
import kotlinx.html.strong
import kotlinx.html.stream.createHTML
import kotlinx.html.td
import kotlinx.html.tr
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentDurationMinutes
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorEmail
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorLicenseNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.PhoneNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.entity.DoctorEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.DoctorActivityStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.domain.webFormat
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.pages.renderTableWithPagination
import ru.bagdasaryan.springkotlin.medkabinet.service.DoctorService
import java.time.LocalDate


data class DoctorFormData(
    val fio: String = "",
    val specializationId: String = "",
    val departmentId: String = "",
    val licenseNumber: String = "",
    val licenseValidUntil: String = "",
    val phone: String = "",
    val email: String = "",
    val appointmentDurationMinutes: String = "20",
    val isActive: String = DoctorActivityStatus.ACTIVE.value
) {
    companion object {
        fun from(doctor: DoctorEntity): DoctorFormData = DoctorFormData(
            fio = doctor.fio.value,
            specializationId = doctor.specializationId.value.toString(),
            departmentId = doctor.departmentId.value.toString(),
            licenseNumber = doctor.licenseNumber.value,
            licenseValidUntil = doctor.licenseValidUntil.toString(),
            phone = doctor.phone?.value.orEmpty(),
            email = doctor.email?.value.orEmpty(),
            appointmentDurationMinutes = doctor.appointmentDurationMinutes.value.toString(),
            isActive = if (doctor.isActive) DoctorActivityStatus.ACTIVE.value else DoctorActivityStatus.INACTIVE.value
        )
    }
}

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

    suspend fun renderCardPage(
        doctor: DoctorEntity,
        specializationName: String,
        departmentName: String,
        departmentRoom: String?,
        departmentPhone: String?
    ): String = doctorCardLayout(
        doctor = doctor,
        specializationName = specializationName,
        departmentName = departmentName,
        departmentRoom = departmentRoom,
        departmentPhone = departmentPhone
    )

    suspend fun renderEditPage(
        doctor: DoctorEntity,
        departments: List<DepartmentEntity>,
        specializations: List<SpecializationEntity>,
        formData: DoctorFormData = DoctorFormData.from(doctor),
        fieldErrors: Map<String, String> = emptyMap()
    ): String = doctorEditLayout(
        doctor = doctor,
        departments = departments,
        specializations = specializations,
        formData = formData,
        fieldErrors = fieldErrors
    )

    private suspend fun findDoctors(q: String?): List<DoctorEntity> {
        if (q.isNullOrBlank()) return doctorService.findAll().getOrThrow()
        val fio = Fio.create(q).getOrThrow()
        return doctorService.findByFio(fio).getOrThrow()
    }

    private fun clinicDoctorsPage(
        doctors: List<DoctorEntity>,
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
                div {
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
                }
            }
        }

        val prevHref = if (q.isBlank()) "/doctors?page=${page - 1}" else "/doctors?page=${page - 1}&q=$q"
        val nextHref = if (q.isBlank()) "/doctors?page=${page + 1}" else "/doctors?page=${page + 1}&q=$q"

        renderTableWithPagination(
            cardTitle = "Список врачей",
            headers = listOf("ФИО", "Email", "Телефон", "Отделение", "Лицензия до", "Действия"),
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
                td { +doctor.fio.value }
                td { +(doctor.email?.value ?: "-") }
                td { +(doctor.phone?.webFormat() ?: "-") }
                td { +doctor.departmentId.value.toString() }
                td { +doctor.licenseValidUntil.toString() }
                td {
                    div("d-flex gap-2 flex-wrap") {
                        a(href = "/doctors/${doctor.id.value}", classes = "btn btn-sm btn-outline-secondary") { +"Карточка" }
                        a(href = "/doctors/${doctor.id.value}/edit", classes = "btn btn-sm btn-outline-primary") { +"Редактировать" }
                    }
                }
            }
        }
    }

    private fun renderDoctorsRows(doctors: List<DoctorEntity>): String =
        doctors.joinToString(separator = "") { renderDoctorRow(it) }

    private fun renderDoctorRow(doctor: DoctorEntity): String = createHTML().tr {
        td { +doctor.fio.value }
        td { +(doctor.email?.value ?: "-") }
        td { +(doctor.phone?.webFormat() ?: "-") }
        td { +doctor.departmentId.value.toString() }
        td { +doctor.licenseValidUntil.toString() }
        td {
            div("d-flex gap-2 flex-wrap") {
                a(href = "/doctors/${doctor.id.value}", classes = "btn btn-sm btn-outline-secondary") { +"Карточка" }
                a(href = "/doctors/${doctor.id.value}/edit", classes = "btn btn-sm btn-outline-primary") { +"Редактировать" }
            }
        }
    }

    private fun doctorCardLayout(
        doctor: DoctorEntity,
        specializationName: String,
        departmentName: String,
        departmentRoom: String?,
        departmentPhone: String?
    ): String = appLayout(
        pageTitle = "Карточка врача",
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
            div {
                h1("h3 mb-1") { +"Карточка врача" }
                div("text-muted") { +"ID: ${doctor.id.value}" }
            }
        }

        div("row g-3") {
            div("col-lg-6") {
                infoCard("Основная информация") {
                    infoRow("ФИО", doctor.fio.value)
                    infoRow("Специализация", specializationName)
                    infoRow("Отделение", departmentName)
                    infoRow("Кабинет", departmentRoom ?: "-")
                    infoRow("Статус", if (doctor.isActive) "Активен" else "Неактивен")
                }
            }

            div("col-lg-6") {
                infoCard("Контакты и лицензия") {
                    infoRow("Телефон", doctor.phone?.webFormat() ?: "-")
                    infoRow("Email", doctor.email?.value ?: "-")
                    infoRow("Номер лицензии", doctor.licenseNumber.value)
                    infoRow("Лицензия до", doctor.licenseValidUntil.toString())
                    infoRow("Длительность приема", "${doctor.appointmentDurationMinutes.value} мин.")
                    infoRow("Контакт отделения", departmentPhone?.let { PhoneNumber(it).webFormat() } ?: "-")
                }
            }

            div("col-lg-6") {
                infoCard("Служебная информация") {
                    infoRow("Создано", doctor.createdAt.webFormat())
                    infoRow("Обновлено", doctor.updatedAt.webFormat())
                }
            }

            div("col-12") {
                div("card shadow-sm") {
                    div("card-body") {
                        div("d-flex gap-2 flex-wrap") {
                            a(href = "/doctors/${doctor.id.value}/edit", classes = "btn btn-primary") { +"Редактировать" }
                            a(href = "/doctors", classes = "btn btn-outline-secondary") { +"К списку" }
                        }
                    }
                }
            }
        }
    }

    private fun doctorEditLayout(
        doctor: DoctorEntity,
        departments: List<DepartmentEntity>,
        specializations: List<SpecializationEntity>,
        formData: DoctorFormData,
        fieldErrors: Map<String, String>
    ): String = appLayout(
        pageTitle = "Редактирование врача",
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
            h1("h3 mb-0") { +"Редактирование врача" }
            a(href = "/doctors/${doctor.id.value}", classes = "btn btn-outline-secondary") { +"К карточке" }
        }

        val formError = fieldErrors["_form"]
        if (!formError.isNullOrBlank()) {
            div("alert alert-danger mb-3") { +formError }
        }

        div("card shadow-sm") {
            div("card-body") {
                form(action = "/doctors/${doctor.id.value}/edit", method = FormMethod.post) {
                    div("row g-3") {
                        textInput("fio", "ФИО", formData.fio, true, "col-12 col-md-6", fieldErrors["fio"])
                        selectInput(
                            name = "specializationId",
                            labelText = "Специализация",
                            current = formData.specializationId,
                            options = specializations.map { Choice(it.id.value.toString(), it.name.value) },
                            required = true,
                            columnClasses = "col-12 col-md-3",
                            errorMessage = fieldErrors["specializationId"]
                        )
                        selectInput(
                            name = "departmentId",
                            labelText = "Отделение",
                            current = formData.departmentId,
                            options = departments.map { Choice(it.id.value.toString(), it.name.value) },
                            required = true,
                            columnClasses = "col-12 col-md-3",
                            errorMessage = fieldErrors["departmentId"]
                        )
                        textInput("licenseNumber", "Номер лицензии", formData.licenseNumber, true, "col-12 col-md-4", fieldErrors["licenseNumber"])
                        dateInput("licenseValidUntil", "Лицензия до", formData.licenseValidUntil, true, "col-12 col-md-4", fieldErrors["licenseValidUntil"])
                        numberInput("appointmentDurationMinutes", "Длительность приема (мин.)", formData.appointmentDurationMinutes, true, "col-12 col-md-4", fieldErrors["appointmentDurationMinutes"])
                        textInput("phone", "Телефон", formData.phone, false, "col-12 col-md-6", fieldErrors["phone"])
                        textInput("email", "Email", formData.email, false, "col-12 col-md-6", fieldErrors["email"])
                        selectInput(
                            name = "isActive",
                            labelText = "Статус",
                            current = formData.isActive,
                            options = DoctorActivityStatus.entries.map { Choice(it.value, it.title) },
                            required = true,
                            columnClasses = "col-12 col-md-4",
                            errorMessage = fieldErrors["isActive"]
                        )

                        div("col-12") {
                            button(type = ButtonType.submit) {
                                classes = setOf("btn", "btn-primary")
                                +"Сохранить изменения"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun FlowContent.infoCard(title: String, body: FlowContent.() -> Unit) {
        div("card shadow-sm h-100") {
            div("card-header") { strong { +title } }
            div("card-body") { body() }
        }
    }

    private fun FlowContent.infoRow(label: String, value: String) {
        div("mb-2") {
            div("text-muted small") { +label }
            div { +value }
        }
    }

    private fun FlowContent.textInput(
        name: String,
        labelText: String,
        value: String,
        required: Boolean,
        columnClasses: String,
        errorMessage: String?
    ) {
        div(columnClasses) {
            label {
                classes = setOf("form-label")
                htmlFor = name
                +labelText
            }
            input(InputType.text) {
                id = name
                this.name = name
                classes = if (!errorMessage.isNullOrBlank()) setOf("form-control", "is-invalid") else setOf("form-control")
                this.value = value
                if (required) this.required = true
            }
            if (!errorMessage.isNullOrBlank()) div("invalid-feedback") { +errorMessage }
        }
    }

    private fun FlowContent.numberInput(
        name: String,
        labelText: String,
        value: String,
        required: Boolean,
        columnClasses: String,
        errorMessage: String?
    ) {
        div(columnClasses) {
            label {
                classes = setOf("form-label")
                htmlFor = name
                +labelText
            }
            input(InputType.number) {
                id = name
                this.name = name
                classes = if (!errorMessage.isNullOrBlank()) setOf("form-control", "is-invalid") else setOf("form-control")
                this.value = value
                if (required) this.required = true
                attributes["min"] = "5"
                attributes["max"] = "180"
                attributes["step"] = "1"
            }
            if (!errorMessage.isNullOrBlank()) div("invalid-feedback") { +errorMessage }
        }
    }

    private fun FlowContent.dateInput(
        name: String,
        labelText: String,
        value: String,
        required: Boolean,
        columnClasses: String,
        errorMessage: String?
    ) {
        div(columnClasses) {
            label {
                classes = setOf("form-label")
                htmlFor = name
                +labelText
            }
            input(InputType.date) {
                id = name
                this.name = name
                classes = if (!errorMessage.isNullOrBlank()) setOf("form-control", "is-invalid") else setOf("form-control")
                this.value = value
                if (required) this.required = true
            }
            if (!errorMessage.isNullOrBlank()) div("invalid-feedback") { +errorMessage }
        }
    }

    private fun FlowContent.selectInput(
        name: String,
        labelText: String,
        current: String,
        options: List<Choice>,
        required: Boolean,
        columnClasses: String,
        errorMessage: String?
    ) {
        div(columnClasses) {
            label {
                classes = setOf("form-label")
                htmlFor = name
                +labelText
            }
            select {
                id = name
                this.name = name
                classes = if (!errorMessage.isNullOrBlank()) setOf("form-select", "is-invalid") else setOf("form-select")
                if (required) this.required = true
                option { value = ""; +"Выберите значение" }
                options.forEach { choice ->
                    option {
                        value = choice.value
                        if (current == choice.value) selected = true
                        +choice.label
                    }
                }
            }
            if (!errorMessage.isNullOrBlank()) div("invalid-feedback") { +errorMessage }
        }
    }

    private data class Choice(val value: String, val label: String)
}
