package ru.bagdasaryan.springkotlin.medkabinet.controller.handler

import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.FormEncType
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
import kotlinx.html.textArea
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.td
import kotlinx.html.thead
import kotlinx.html.tbody
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressApartment
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressBuilding
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressCity
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressStreet
import ru.bagdasaryan.springkotlin.medkabinet.domain.CountryCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.InsuranceCompany
import ru.bagdasaryan.springkotlin.medkabinet.domain.InsuranceNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientEmail
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientGender
import ru.bagdasaryan.springkotlin.medkabinet.domain.PhoneNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientMedicalHistoryEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.AddressPostalCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.webFormat
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.MedicalCardNumber
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.pages.renderTableWithPagination
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PatientFormData(
    val fio: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val phone: String = "",
    val email: String = "",
    val addressCountry: String = "RU",
    val addressCity: String = "",
    val addressStreet: String = "",
    val addressBuilding: String = "",
    val addressApartment: String = "",
    val addressPostalCode: String = "",
    val insuranceNumber: String = "",
    val insuranceCompany: String = "",
    val insuranceValidUntil: String = "",
    val medicalCardNumber: String = ""
) {
    companion object {
        fun from(patient: PatientEntity): PatientFormData = PatientFormData(
            fio = patient.fio.value,
            dateOfBirth = patient.dateOfBirth.toString(),
            gender = patient.gender.value,
            phone = patient.phone.value,
            email = patient.email?.value.orEmpty(),
            addressCountry = patient.addressCountry.value,
            addressCity = patient.addressCity.value,
            addressStreet = patient.addressStreet.value,
            addressBuilding = patient.addressBuilding.value,
            addressApartment = patient.addressApartment?.value.orEmpty(),
            addressPostalCode = patient.addressPostalCode?.value.orEmpty(),
            insuranceNumber = patient.insuranceNumber?.value.orEmpty(),
            insuranceCompany = patient.insuranceCompany?.value.orEmpty(),
            insuranceValidUntil = patient.insuranceValidUntil?.toString().orEmpty(),
            medicalCardNumber = patient.medicalCardNumber.value
        )
    }
}

data class MedicalHistoryFormData(
    val eventDate: String = "",
    val complaint: String = "",
    val diagnosis: String = "",
    val recommendation: String = "",
    val note: String = ""
) {
    companion object {
        fun from(history: PatientMedicalHistoryEntity): MedicalHistoryFormData = MedicalHistoryFormData(
            eventDate = history.eventDate.toString(),
            complaint = history.complaint.value,
            diagnosis = history.diagnosis.value,
            recommendation = history.recommendation.value,
            note = history.note?.value.orEmpty()
        )
    }
}

@Component
class PatientPageHandler {
    private val pageSize = 30
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    fun renderListPage(
        patients: List<PatientEntity>,
        page: Int,
        errorMessage: String? = null,
        canCreatePatient: Boolean = true,
        canEditPatient: Boolean = true,
        canImportCsv: Boolean = false
    ): String {
        val safePage = page.coerceAtLeast(1)
        val fromIndex = (safePage - 1) * pageSize
        val toIndex = (fromIndex + pageSize).coerceAtMost(patients.size)
        val pagePatients = if (fromIndex >= patients.size) emptyList() else patients.subList(fromIndex, toIndex)

        return appLayout(
            pageTitle = "Пациенты",
            brandHref = "/",
            nav = listOf(
                NavItem("Главная", "/"),
                NavItem("Расписание", "/schedule"),
                NavItem("Пациенты", "/patients", active = true),
                NavItem("Врачи", "/doctors"),
                NavItem("Записи", "/appointments")
            )
        ) {
            div {
                classes = setOf("d-flex", "justify-content-between", "align-items-center", "mb-3")
                h1("h3 mb-0") { +"Пациенты" }
                if (canCreatePatient) {
                    a(href = "/patients/new", classes = "btn btn-primary") { +"Добавить пациента" }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                div("alert alert-danger") { +errorMessage }
            }

            if (canImportCsv) {
                div("card shadow-sm mb-3") {
                    div("card-body") {
                        div("d-flex justify-content-between align-items-center flex-wrap gap-3") {
                            div {
                                strong { +"Импорт CSV" }
                                div("text-muted small") { +"Поддерживается CSV, выгруженный из карточки пациента" }
                            }
                            form(action = "/patients/import", method = FormMethod.post) {
                                encType = FormEncType.multipartFormData
                                classes = setOf("d-flex", "gap-2", "align-items-center", "flex-wrap")
                                input(InputType.file) {
                                    name = "file"
                                    classes = setOf("form-control")
                                    attributes["accept"] = ".csv,text/csv"
                                }
                                button(type = ButtonType.submit) {
                                    classes = setOf("btn", "btn-outline-primary")
                                    +"Импортировать"
                                }
                            }
                        }
                    }
                }
            }

            renderTableWithPagination(
                cardTitle = "Список пациентов",
                headers = listOf("ФИО", "Дата рождения", "Пол", "Телефон", "Карта", "Действия"),
                rows = pagePatients,
                page = safePage,
                hasPrev = safePage > 1,
                hasNext = toIndex < patients.size,
                prevHref = "/patients?page=${safePage - 1}",
                nextHref = "/patients?page=${safePage + 1}",
                emptyMessage = "Пациенты не найдены"
            ) { patient ->
                tr {
                    td {
                        a(href = "/patients/${patient.id.value}") { +patient.fio.value }
                    }
                    td { +patient.dateOfBirth.toString() }
                    td { +patient.gender.title }
                    td { +patient.phone.webFormat() }
                    td { +patient.medicalCardNumber.value }
                    td {
                        if (canEditPatient) {
                            a(href = "/patients/${patient.id.value}/edit", classes = "btn btn-sm btn-outline-primary") {
                                +"Редактировать"
                            }
                        } else {
                            span("text-muted small") { +"Только просмотр" }
                        }
                    }
                }
            }
        }
    }

    fun renderCreatePage(formData: PatientFormData = PatientFormData(), fieldErrors: Map<String, String> = emptyMap()): String =
        renderFormPage(
            title = "Добавление пациента",
            action = "/patients",
            submitText = "Создать пациента",
            formData = formData,
            fieldErrors = fieldErrors
        )

    fun renderEditPage(
        patient: PatientEntity,
        formData: PatientFormData = PatientFormData.from(patient),
        fieldErrors: Map<String, String> = emptyMap()
    ): String =
        renderFormPage(
            title = "Редактирование пациента",
            action = "/patients/${patient.id.value}/edit",
            submitText = "Сохранить изменения",
            formData = formData,
            fieldErrors = fieldErrors,
            backHref = "/patients/${patient.id.value}"
        )

    fun renderCardPage(
        patient: PatientEntity,
        medicalHistory: List<PatientMedicalHistoryEntity> = emptyList(),
        editableHistoryIds: Set<Int> = emptySet(),
        canEditPatient: Boolean = true,
        canCreatePatient: Boolean = true,
        canBookAppointment: Boolean = true,
        canCreateMedicalHistory: Boolean = false
    ): String =
        appLayout(
            pageTitle = "Карточка пациента",
            brandHref = "/",
            nav = listOf(
                NavItem("Главная", "/"),
                NavItem("Расписание", "/schedule"),
                NavItem("Пациенты", "/patients", active = true),
                NavItem("Врачи", "/doctors"),
                NavItem("Записи", "/appointments")
            )
        ) {
            div {
                classes = setOf("d-flex", "justify-content-between", "align-items-center", "mb-3")
                div {
                    h1("h3 mb-1") { +"Карточка пациента" }
                    div("text-muted") { +"ID: ${patient.id.value}" }
                }
                a(href = "/patients", classes = "btn btn-outline-secondary") { +"К списку" }
            }

            div("row g-3") {
                div("col-lg-6") {
                    infoCard("Основная информация") {
                        infoRow("ФИО", patient.fio.value)
                        infoRow("Дата рождения", patient.dateOfBirth.toString())
                        infoRow("Пол", patient.gender.title)
                        infoRow("Номер медкарты", patient.medicalCardNumber.value)
                        infoRow("Дата регистрации", formatDateTime(patient.registrationDate))
                    }
                }

                div("col-lg-6") {
                    infoCard("Контакты и адрес") {
                        infoRow("Телефон", patient.phone.webFormat())
                        infoRow("Email", patient.email?.value ?: "-")
                        infoRow("Страна", patient.addressCountry.value)
                        infoRow("Город", patient.addressCity.value)
                        infoRow("Улица", patient.addressStreet.value)
                        infoRow("Дом", patient.addressBuilding.value)
                        infoRow("Квартира", patient.addressApartment?.value ?: "-")
                        infoRow("Почтовый индекс", patient.addressPostalCode?.value ?: "-")
                    }
                }

                div("col-lg-6") {
                    infoCard("Страхование") {
                        infoRow("Номер полиса", patient.insuranceNumber?.value ?: "-")
                        infoRow("Страховая компания", patient.insuranceCompany?.value ?: "-")
                        infoRow("Полис действителен до", patient.insuranceValidUntil?.toString() ?: "-")
                    }
                }

                div("col-12") {
                    infoCard("История болезни") {
                        if (canCreateMedicalHistory) {
                            div("d-flex justify-content-end mb-3") {
                                a(
                                    href = "/patients/${patient.id.value}/history/new",
                                    classes = "btn btn-sm btn-outline-success"
                                ) {
                                    +"Добавить запись"
                                }
                            }
                        }
                        if (medicalHistory.isEmpty()) {
                            div("text-muted") { +"Записей истории болезни пока нет" }
                        } else {
                            div("table-responsive") {
                                table("table table-striped table-hover align-middle mb-0") {
                                    thead {
                                        tr {
                                            th { +"Дата" }
                                            th { +"Врач" }
                                            th { +"Жалоба" }
                                            th { +"Диагноз" }
                                            th { +"Рекомендации" }
                                            th { +"Примечание" }
                                            if (editableHistoryIds.isNotEmpty()) {
                                                th { +"Действия" }
                                            }
                                        }
                                    }
                                    tbody {
                                        medicalHistory.forEach { entry ->
                                            tr {
                                                td { +entry.eventDate.toString() }
                                                td { +entry.doctorFio.value }
                                                td { +entry.complaint.value }
                                                td { +entry.diagnosis.value }
                                                td { +entry.recommendation.value }
                                                td { +(entry.note?.value ?: "-") }
                                                if (editableHistoryIds.contains(entry.id.value)) {
                                                    td {
                                                        a(
                                                            href = "/patients/${patient.id.value}/history/${entry.id.value}/edit",
                                                            classes = "btn btn-sm btn-outline-primary"
                                                        ) {
                                                            +"Редактировать"
                                                        }
                                                    }
                                                } else if (editableHistoryIds.isNotEmpty()) {
                                                    td { span("text-muted small") { +"Недоступно" } }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                div("col-lg-6") {
                    infoCard("Служебная информация") {
                        infoRow("Создано", formatDateTime(patient.createdAt))
                        infoRow("Обновлено", formatDateTime(patient.updatedAt))
                        infoRow("Удалено", patient.deletedAt?.let { formatDateTime(it) } ?: "-")
                    }
                }

                div("col-12") {
                    div("card shadow-sm") {
                        div("card-body") {
                            div("d-flex gap-2 flex-wrap") {
                                if (canEditPatient) {
                                    a(href = "/patients/${patient.id.value}/edit", classes = "btn btn-primary") {
                                        +"Редактировать"
                                    }
                                }
                                if (canCreatePatient) {
                                    a(href = "/patients/new", classes = "btn btn-outline-success") {
                                        +"Добавить пациента"
                                    }
                                }
                                if (canBookAppointment) {
                                    a(href = "/appointments?patientId=${patient.id.value}", classes = "btn btn-outline-secondary") {
                                        +"Записать на прием"
                                    }
                                }
                                a(href = "/patients/${patient.id.value}/export", classes = "btn btn-outline-dark") {
                                    +"Экспорт CSV"
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun renderFormPage(
        title: String,
        action: String,
        submitText: String,
        formData: PatientFormData,
        fieldErrors: Map<String, String>,
        backHref: String = "/patients"
    ): String = appLayout(
        pageTitle = title,
        brandHref = "/",
        nav = listOf(
            NavItem("Главная", "/"),
            NavItem("Расписание", "/schedule"),
            NavItem("Пациенты", "/patients", active = true),
            NavItem("Врачи", "/doctors"),
            NavItem("Записи", "/appointments")
        )
        ) {
        div {
            classes = setOf("d-flex", "justify-content-between", "align-items-center", "mb-3")
            h1("h3 mb-0") { +title }
            a(href = backHref, classes = "btn btn-outline-secondary") { +"К списку" }
        }

        val formError = fieldErrors["_form"]
        if (!formError.isNullOrBlank()) {
            div("alert alert-danger mb-3") { +formError }
        }

        div("card shadow-sm") {
            div("card-body") {
                form(action = action, method = FormMethod.post) {
                    div("row g-3") {
                        textInput("fio", "ФИО", formData.fio, true, "col-12 col-md-6", fieldErrors["fio"])
                        dateInput("dateOfBirth", "Дата рождения", formData.dateOfBirth, true, "col-12 col-md-3", fieldErrors["dateOfBirth"])
                        genderSelect("gender", "Пол", formData.gender, "col-12 col-md-3", fieldErrors["gender"])
                        textInput("phone", "Телефон", formData.phone, true, "col-12 col-md-6", fieldErrors["phone"])
                        textInput("email", "Email", formData.email, false, "col-12 col-md-6", fieldErrors["email"])

                        textInput("addressCountry", "Страна", formData.addressCountry, true, "col-12 col-md-2", fieldErrors["addressCountry"])
                        textInput("addressCity", "Город", formData.addressCity, true, "col-12 col-md-4", fieldErrors["addressCity"])
                        textInput("addressStreet", "Улица", formData.addressStreet, true, "col-12 col-md-4", fieldErrors["addressStreet"])
                        textInput("addressBuilding", "Дом", formData.addressBuilding, true, "col-12 col-md-2", fieldErrors["addressBuilding"])

                        textInput("addressApartment", "Квартира", formData.addressApartment, false, "col-12 col-md-3", fieldErrors["addressApartment"])
                        textInput("addressPostalCode", "Почтовый индекс", formData.addressPostalCode, false, "col-12 col-md-3", fieldErrors["addressPostalCode"])
                        textInput("insuranceNumber", "Номер полиса", formData.insuranceNumber, false, "col-12 col-md-3", fieldErrors["insuranceNumber"])
                        textInput("insuranceCompany", "Страховая компания", formData.insuranceCompany, false, "col-12 col-md-3", fieldErrors["insuranceCompany"])
                        dateInput("insuranceValidUntil", "Полис действителен до", formData.insuranceValidUntil, false, "col-12 col-md-3", fieldErrors["insuranceValidUntil"])
                        textInput("medicalCardNumber", "Номер медкарты", formData.medicalCardNumber, true, "col-12 col-md-3", fieldErrors["medicalCardNumber"])

                        div("col-12") {
                            button(type = ButtonType.submit) {
                                classes = setOf("btn", "btn-primary")
                                +submitText
                            }
                        }
                    }
                }
            }
        }
    }

    fun renderHistoryEditPage(
        patient: PatientEntity,
        history: PatientMedicalHistoryEntity,
        formData: MedicalHistoryFormData = MedicalHistoryFormData.from(history),
        fieldErrors: Map<String, String> = emptyMap()
    ): String = renderMedicalHistoryFormPage(
        pageTitle = "Редактирование истории болезни",
        heading = "Редактирование истории болезни",
        patient = patient,
        action = "/patients/${patient.id.value}/history/${history.id.value}/edit",
        formData = formData,
        fieldErrors = fieldErrors
    )

    fun renderHistoryCreatePage(
        patient: PatientEntity,
        formData: MedicalHistoryFormData = MedicalHistoryFormData(),
        fieldErrors: Map<String, String> = emptyMap()
    ): String = renderMedicalHistoryFormPage(
        pageTitle = "Добавление записи в историю болезни",
        heading = "Добавление записи в историю болезни",
        patient = patient,
        action = "/patients/${patient.id.value}/history",
        formData = formData,
        fieldErrors = fieldErrors
    )

    private fun renderMedicalHistoryFormPage(
        pageTitle: String,
        heading: String,
        patient: PatientEntity,
        action: String,
        formData: MedicalHistoryFormData,
        fieldErrors: Map<String, String>
    ): String = appLayout(
        pageTitle = pageTitle,
        brandHref = "/",
        nav = listOf(
            NavItem("Главная", "/"),
            NavItem("Расписание", "/schedule"),
            NavItem("Пациенты", "/patients", active = true),
            NavItem("Врачи", "/doctors"),
            NavItem("Записи", "/appointments")
        )
    ) {
        div {
            classes = setOf("d-flex", "justify-content-between", "align-items-center", "mb-3")
            div {
                h1("h3 mb-0") { +heading }
                div("text-muted") { +patient.fio.value }
            }
            a(href = "/patients/${patient.id.value}", classes = "btn btn-outline-secondary") { +"К карточке" }
        }

        val formError = fieldErrors["_form"]
        if (!formError.isNullOrBlank()) {
            div("alert alert-danger mb-3") { +formError }
        }

        div("card shadow-sm") {
            div("card-body") {
                form(action = action, method = FormMethod.post) {
                    div("row g-3") {
                        dateInput("eventDate", "Дата приема", formData.eventDate, true, "col-12 col-md-4", fieldErrors["eventDate"])
                        textInput("complaint", "Жалоба", formData.complaint, true, "col-12", fieldErrors["complaint"])
                        textInput("diagnosis", "Диагноз", formData.diagnosis, true, "col-12", fieldErrors["diagnosis"])
                        textInput("recommendation", "Рекомендации", formData.recommendation, true, "col-12", fieldErrors["recommendation"])
                        textAreaInput("note", "Примечание", formData.note, "col-12", fieldErrors["note"])

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
                val hasError = !errorMessage.isNullOrBlank()
                classes = if (hasError) {
                    setOf("form-control", "is-invalid")
                } else {
                    setOf("form-control")
                }
                this.value = value
                if (required) this.required = true
            }
            if (!errorMessage.isNullOrBlank()) {
                div("invalid-feedback") { +errorMessage }
            }
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
                val hasError = !errorMessage.isNullOrBlank()
                classes = if (hasError) {
                    setOf("form-control", "is-invalid")
                } else {
                    setOf("form-control")
                }
                this.value = value
                if (required) this.required = true
            }
            if (!errorMessage.isNullOrBlank()) {
                div("invalid-feedback") { +errorMessage }
            }
        }
    }

    private fun FlowContent.genderSelect(
        name: String,
        labelText: String,
        current: String,
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
                val hasError = !errorMessage.isNullOrBlank()
                classes = if (hasError) {
                    setOf("form-select", "is-invalid")
                } else {
                    setOf("form-select")
                }
                required = true
                option { value = ""; +"Выберите пол" }
                PatientGender.entries.forEach { gender ->
                    option {
                        value = gender.value
                        if (current.equals(gender.value, ignoreCase = true) || current.equals(gender.name, ignoreCase = true)) selected = true
                        +gender.title
                    }
                }
            }
            if (!errorMessage.isNullOrBlank()) {
                div("invalid-feedback") { +errorMessage }
            }
        }
    }

    private fun FlowContent.textAreaInput(
        name: String,
        labelText: String,
        value: String,
        columnClasses: String,
        errorMessage: String?
    ) {
        div(columnClasses) {
            label {
                classes = setOf("form-label")
                htmlFor = name
                +labelText
            }
            textArea {
                id = name
                this.name = name
                attributes["rows"] = "4"
                val hasError = !errorMessage.isNullOrBlank()
                classes = if (hasError) {
                    setOf("form-control", "is-invalid")
                } else {
                    setOf("form-control")
                }
                +value
            }
            if (!errorMessage.isNullOrBlank()) {
                div("invalid-feedback") { +errorMessage }
            }
        }
    }

    private fun formatDateTime(value: LocalDateTime): String = value.format(dateTimeFormatter)
}
