package ru.bagdasaryan.springkotlin.medkabinet.service

import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressApartment
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressBuilding
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressCity
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressStreet
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentType
import ru.bagdasaryan.springkotlin.medkabinet.domain.CountryCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.InsuranceCompany
import ru.bagdasaryan.springkotlin.medkabinet.domain.InsuranceNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryComplaint
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryDiagnosis
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryRecommendation
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientEmail
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientGender
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientMedicalHistoryEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.PhoneNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.AddressPostalCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.MedicalCardNumber
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.PatientCsvTransferPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindPatientAppointmentsForExportQuery
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class PatientCsvTransferService(
    private val patientService: PatientService,
    private val patientMedicalHistoryService: PatientMedicalHistoryService,
    private val doctorService: DoctorService,
    private val findPatientAppointmentsForExportQuery: FindPatientAppointmentsForExportQuery,
    private val patientCsvTransferPersister: PatientCsvTransferPersister
) {
    data class ExportedPatientCsv(
        val fileName: String,
        val content: String
    )

    private data class ParsedCsvRow(
        val number: Int,
        val values: Map<String, String>
    ) {
        fun type(): String = values.getValue("record_type").trim().uppercase()
        fun get(name: String): String = values[name].orEmpty().trim()
        fun require(name: String): String {
            val value = get(name)
            require(value.isNotBlank()) { "В строке $number отсутствует обязательное поле '$name'" }
            return value
        }
    }

    suspend fun exportPatientCsv(patientId: PatientId): Result<ExportedPatientCsv> = runCatching {
        val patient = patientService.findById(patientId).getOrThrow()
            ?: throw NoSuchElementException("Не найден пациент с id ${patientId.value}")
        val medicalHistory = patientMedicalHistoryService.findByPatientId(patientId).getOrThrow()
        val appointments = findPatientAppointmentsForExportQuery.findByPatientId(patientId).getOrThrow()

        val csv = buildString {
            appendCsvRow(CSV_HEADERS)
            appendCsvRow(
                csvRowValues(
                    "record_type" to "PATIENT",
                    "patient_id" to patient.id.value.toString(),
                    "patient_fio" to patient.fio.value,
                    "date_of_birth" to patient.dateOfBirth.toString(),
                    "gender" to patient.gender.value,
                    "phone" to patient.phone.value,
                    "email" to patient.email?.value,
                    "address_country" to patient.addressCountry.value,
                    "address_city" to patient.addressCity.value,
                    "address_street" to patient.addressStreet.value,
                    "address_building" to patient.addressBuilding.value,
                    "address_apartment" to patient.addressApartment?.value,
                    "address_postal_code" to patient.addressPostalCode?.value,
                    "insurance_number" to patient.insuranceNumber?.value,
                    "insurance_company" to patient.insuranceCompany?.value,
                    "insurance_valid_until" to patient.insuranceValidUntil?.toString(),
                    "medical_card_number" to patient.medicalCardNumber.value,
                    "registration_date" to patient.registrationDate.toString()
                )
            )

            medicalHistory.forEach { row ->
                appendCsvRow(
                    csvRowValues(
                        "record_type" to "HISTORY",
                        "patient_id" to patient.id.value.toString(),
                        "patient_fio" to patient.fio.value,
                        "date_of_birth" to patient.dateOfBirth.toString(),
                        "gender" to patient.gender.value,
                        "phone" to patient.phone.value,
                        "email" to patient.email?.value,
                        "address_country" to patient.addressCountry.value,
                        "address_city" to patient.addressCity.value,
                        "address_street" to patient.addressStreet.value,
                        "address_building" to patient.addressBuilding.value,
                        "address_apartment" to patient.addressApartment?.value,
                        "address_postal_code" to patient.addressPostalCode?.value,
                        "insurance_number" to patient.insuranceNumber?.value,
                        "insurance_company" to patient.insuranceCompany?.value,
                        "insurance_valid_until" to patient.insuranceValidUntil?.toString(),
                        "medical_card_number" to patient.medicalCardNumber.value,
                        "registration_date" to patient.registrationDate.toString(),
                        "history_id" to row.id.value.toString(),
                        "history_event_date" to row.eventDate.toString(),
                        "history_doctor_id" to row.doctorId.value.toString(),
                        "history_doctor_fio" to row.doctorFio.value,
                        "history_complaint" to row.complaint.value,
                        "history_diagnosis" to row.diagnosis.value,
                        "history_recommendation" to row.recommendation.value,
                        "history_note" to row.note?.value
                    )
                )
            }

            appointments.forEach { row ->
                appendCsvRow(
                    csvRowValues(
                        "record_type" to "APPOINTMENT",
                        "patient_id" to patient.id.value.toString(),
                        "patient_fio" to patient.fio.value,
                        "date_of_birth" to patient.dateOfBirth.toString(),
                        "gender" to patient.gender.value,
                        "phone" to patient.phone.value,
                        "email" to patient.email?.value,
                        "address_country" to patient.addressCountry.value,
                        "address_city" to patient.addressCity.value,
                        "address_street" to patient.addressStreet.value,
                        "address_building" to patient.addressBuilding.value,
                        "address_apartment" to patient.addressApartment?.value,
                        "address_postal_code" to patient.addressPostalCode?.value,
                        "insurance_number" to patient.insuranceNumber?.value,
                        "insurance_company" to patient.insuranceCompany?.value,
                        "insurance_valid_until" to patient.insuranceValidUntil?.toString(),
                        "medical_card_number" to patient.medicalCardNumber.value,
                        "registration_date" to patient.registrationDate.toString(),
                        "appointment_id" to row.appointmentId.value.toString(),
                        "appointment_doctor_id" to row.doctorId.value.toString(),
                        "appointment_doctor_fio" to row.doctorFio.value,
                        "appointment_date" to row.appointmentDate.toString(),
                        "start_time" to row.startTime.value.toString(),
                        "end_time" to row.endTime.value.toString(),
                        "appointment_type" to row.appointmentType.value,
                        "appointment_status" to row.appointmentStatus.value,
                        "appointment_note" to row.appointmentNote?.value
                    )
                )
            }
        }

        ExportedPatientCsv(
            fileName = "patient_${patient.id.value}_${patient.medicalCardNumber.value}.csv",
            content = csv
        )
    }

    suspend fun importPatientCsv(content: String): Result<PatientId> = runCatching {
        val rows = parseCsv(content)
        require(rows.isNotEmpty()) { "CSV-файл пуст" }

        val patientRows = rows.filter { it.type() == "PATIENT" }
        require(patientRows.size == 1) { "CSV должен содержать ровно одну строку с типом PATIENT" }
        val patientRow = patientRows.single()
        val patientData = buildPatientData(patientRow)

        rows.forEach { row ->
            val rowMedicalCard = row.get("medical_card_number")
            if (rowMedicalCard.isNotBlank()) {
                require(rowMedicalCard == patientData.medicalCardNumber.value) {
                    "Все строки CSV должны относиться к одному пациенту"
                }
            }
        }

        val existingPatient = patientService.findByMedicalCardNumber(patientData.medicalCardNumber).getOrThrow()
        val patientId = if (existingPatient != null) {
            patientService.updatePatient(
                id = existingPatient.id,
                fio = patientData.fio,
                dateOfBirth = patientData.dateOfBirth,
                gender = patientData.gender,
                phone = patientData.phone,
                email = patientData.email,
                addressCountry = patientData.addressCountry,
                addressCity = patientData.addressCity,
                addressStreet = patientData.addressStreet,
                addressBuilding = patientData.addressBuilding,
                addressApartment = patientData.addressApartment,
                addressPostalCode = patientData.addressPostalCode,
                insuranceNumber = patientData.insuranceNumber,
                insuranceCompany = patientData.insuranceCompany,
                insuranceValidUntil = patientData.insuranceValidUntil,
                medicalCardNumber = patientData.medicalCardNumber
            ).getOrThrow()
            existingPatient.id
        } else {
            patientService.createPatient(
                fio = patientData.fio,
                dateOfBirth = patientData.dateOfBirth,
                gender = patientData.gender,
                phone = patientData.phone,
                email = patientData.email,
                addressCountry = patientData.addressCountry,
                addressCity = patientData.addressCity,
                addressStreet = patientData.addressStreet,
                addressBuilding = patientData.addressBuilding,
                addressApartment = patientData.addressApartment,
                addressPostalCode = patientData.addressPostalCode,
                insuranceNumber = patientData.insuranceNumber,
                insuranceCompany = patientData.insuranceCompany,
                insuranceValidUntil = patientData.insuranceValidUntil,
                medicalCardNumber = patientData.medicalCardNumber
            ).getOrThrow()
        }

        importMedicalHistoryRows(patientId, rows.filter { it.type() == "HISTORY" })
        importAppointmentRows(patientId, rows.filter { it.type() == "APPOINTMENT" })

        patientId
    }

    private suspend fun importMedicalHistoryRows(patientId: PatientId, rows: List<ParsedCsvRow>) {
        if (rows.isEmpty()) return

        val existingRows = patientMedicalHistoryService.findByPatientId(patientId).getOrThrow().toMutableList()
        rows.forEach { row ->
            val doctorId = DoctorId(row.require("history_doctor_id").toInt())
            require(doctorService.findById(doctorId).getOrThrow() != null) {
                "Не найден врач с id ${doctorId.value} для строки ${row.number}"
            }

            val eventDate = LocalDate.parse(row.require("history_event_date"))
            val complaint = MedicalHistoryComplaint.create(row.require("history_complaint")).getOrThrow()
            val diagnosis = MedicalHistoryDiagnosis.create(row.require("history_diagnosis")).getOrThrow()
            val recommendation = MedicalHistoryRecommendation.create(row.require("history_recommendation")).getOrThrow()
            val note = row.get("history_note").takeIf { it.isNotBlank() }?.let {
                MedicalHistoryNote.create(it).getOrThrow()
            }

            val duplicate = existingRows.any { existing ->
                existing.doctorId == doctorId &&
                    existing.eventDate == eventDate &&
                    existing.complaint.value == complaint.value &&
                    existing.diagnosis.value == diagnosis.value &&
                    existing.recommendation.value == recommendation.value &&
                    (existing.note?.value ?: "") == (note?.value ?: "")
            }
            if (!duplicate) {
                val now = LocalDateTime.now()
                patientMedicalHistoryService.createHistory(
                    patientId = patientId,
                    doctorId = doctorId,
                    eventDate = eventDate,
                    complaint = complaint,
                    diagnosis = diagnosis,
                    recommendation = recommendation,
                    note = note,
                    createdAt = now,
                    updatedAt = now
                ).getOrThrow()

                existingRows += PatientMedicalHistoryEntity(
                    id = patientMedicalHistoryService.findByPatientId(patientId).getOrThrow().last().id,
                    patientId = patientId,
                    doctorId = doctorId,
                    doctorFio = doctorService.findById(doctorId).getOrThrow()!!.fio,
                    eventDate = eventDate,
                    complaint = complaint,
                    diagnosis = diagnosis,
                    recommendation = recommendation,
                    note = note,
                    createdAt = now,
                    updatedAt = now
                )
            }
        }
    }

    private suspend fun importAppointmentRows(patientId: PatientId, rows: List<ParsedCsvRow>) {
        if (rows.isEmpty()) return

        val importedRows = rows.map { row ->
            val doctorId = DoctorId(row.require("appointment_doctor_id").toInt())
            require(doctorService.findById(doctorId).getOrThrow() != null) {
                "Не найден врач с id ${doctorId.value} для строки ${row.number}"
            }
            PatientCsvTransferPersister.ImportedAppointmentRow(
                doctorId = doctorId,
                appointmentDate = LocalDate.parse(row.require("appointment_date")),
                startTime = LocalTime.parse(row.require("start_time")),
                endTime = LocalTime.parse(row.require("end_time")),
                appointmentType = AppointmentType.create(row.require("appointment_type")).getOrThrow(),
                appointmentStatus = AppointmentStatus.create(row.require("appointment_status")).getOrThrow(),
                appointmentNote = row.get("appointment_note").takeIf { it.isNotBlank() }?.let {
                    AppointmentNote.create(it).getOrThrow()
                }
            )
        }.distinctBy {
            listOf(
                it.doctorId.value,
                it.appointmentDate,
                it.startTime,
                it.endTime,
                it.appointmentType.value,
                it.appointmentStatus.value,
                it.appointmentNote?.value.orEmpty()
            )
        }

        patientCsvTransferPersister.importAppointments(patientId, importedRows).getOrThrow()
    }

    private fun buildPatientData(row: ParsedCsvRow): ImportedPatientData {
        val fio = Fio.create(row.require("patient_fio")).getOrThrow()
        val gender = PatientGender.from(row.require("gender"))
        val medicalCardNumber = MedicalCardNumber(row.require("medical_card_number"))
        return ImportedPatientData(
            fio = fio,
            dateOfBirth = LocalDate.parse(row.require("date_of_birth")),
            gender = gender,
            phone = PhoneNumber(row.require("phone")),
            email = row.get("email").takeIf { it.isNotBlank() }?.let(::PatientEmail),
            addressCountry = CountryCode(row.require("address_country").uppercase()),
            addressCity = AddressCity(row.require("address_city")),
            addressStreet = AddressStreet(row.require("address_street")),
            addressBuilding = AddressBuilding(row.require("address_building")),
            addressApartment = row.get("address_apartment").takeIf { it.isNotBlank() }?.let(::AddressApartment),
            addressPostalCode = row.get("address_postal_code").takeIf { it.isNotBlank() }?.let(::AddressPostalCode),
            insuranceNumber = row.get("insurance_number").takeIf { it.isNotBlank() }?.let(::InsuranceNumber),
            insuranceCompany = row.get("insurance_company").takeIf { it.isNotBlank() }?.let(::InsuranceCompany),
            insuranceValidUntil = row.get("insurance_valid_until").takeIf { it.isNotBlank() }?.let(LocalDate::parse),
            medicalCardNumber = medicalCardNumber
        )
    }

    private fun parseCsv(content: String): List<ParsedCsvRow> {
        val normalized = content.removePrefix(UTF8_BOM.toString()).replace("\r\n", "\n").trim()
        require(normalized.isNotBlank()) { "CSV-файл пуст" }

        val lines = normalized.split('\n').filter { it.isNotBlank() }
        require(lines.isNotEmpty()) { "CSV-файл пуст" }
        val headers = parseCsvLine(lines.first())
        require(headers == CSV_HEADERS) { "CSV имеет неподдерживаемую структуру" }

        return lines.drop(1).mapIndexed { index, line ->
            val values = parseCsvLine(line)
            require(values.size <= headers.size) {
                "Строка ${index + 2} имеет некорректное количество колонок"
            }
            val paddedValues = if (values.size == headers.size) {
                values
            } else {
                values + List(headers.size - values.size) { "" }
            }
            ParsedCsvRow(
                number = index + 2,
                values = normalizeLegacyRow(headers.zip(paddedValues).toMap())
            )
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val values = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var index = 0
        while (index < line.length) {
            val char = line[index]
            when {
                char == '"' && inQuotes && index + 1 < line.length && line[index + 1] == '"' -> {
                    current.append('"')
                    index++
                }
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    values += current.toString()
                    current.setLength(0)
                }
                else -> current.append(char)
            }
            index++
        }
        values += current.toString()
        return values
    }


    private fun normalizeLegacyRow(values: Map<String, String>): Map<String, String> {
        if (values["record_type"] != "APPOINTMENT") return values

        val appointmentDoctorId = values["appointment_doctor_id"].orEmpty()
        val legacyAppointmentId = values["history_note"].orEmpty()
        val shiftedDoctorId = values["appointment_id"].orEmpty()
        val looksShifted = appointmentDoctorId.isNotBlank() && appointmentDoctorId.toIntOrNull() == null &&
            legacyAppointmentId.toIntOrNull() != null && shiftedDoctorId.toIntOrNull() != null
        if (!looksShifted) return values

        return values.toMutableMap().apply {
            this["history_note"] = ""
            this["appointment_id"] = legacyAppointmentId
            this["appointment_doctor_id"] = shiftedDoctorId
            this["appointment_doctor_fio"] = values["appointment_doctor_id"].orEmpty()
            this["appointment_date"] = values["appointment_doctor_fio"].orEmpty()
            this["start_time"] = values["appointment_date"].orEmpty()
            this["end_time"] = values["start_time"].orEmpty()
            this["appointment_type"] = values["end_time"].orEmpty()
            this["appointment_status"] = values["appointment_type"].orEmpty()
            this["appointment_note"] = values["appointment_status"].orEmpty()
        }
    }

    private fun StringBuilder.appendCsvRow(values: List<String?>) {
        require(values.size <= CSV_HEADERS.size) { "Внутренняя ошибка формирования CSV: слишком много колонок" }
        val paddedValues = if (values.size == CSV_HEADERS.size) {
            values
        } else {
            values + List(CSV_HEADERS.size - values.size) { null }
        }
        append(paddedValues.joinToString(separator = ",") { value ->
            val normalized = value.orEmpty().replace("\r", " ").replace("\n", " ")
            '"' + normalized.replace("\"", "\"\"") + '"'
        })
        append('\n')
    }

    private fun csvRowValues(vararg pairs: Pair<String, String?>): List<String?> {
        val values = pairs.toMap()
        return CSV_HEADERS.map { header -> values[header] }
    }

    private data class ImportedPatientData(
        val fio: Fio,
        val dateOfBirth: LocalDate,
        val gender: PatientGender,
        val phone: PhoneNumber,
        val email: PatientEmail?,
        val addressCountry: CountryCode,
        val addressCity: AddressCity,
        val addressStreet: AddressStreet,
        val addressBuilding: AddressBuilding,
        val addressApartment: AddressApartment?,
        val addressPostalCode: AddressPostalCode?,
        val insuranceNumber: InsuranceNumber?,
        val insuranceCompany: InsuranceCompany?,
        val insuranceValidUntil: LocalDate?,
        val medicalCardNumber: MedicalCardNumber
    )

    companion object {
        private val UTF8_BOM = '\uFEFF'
        private val CSV_HEADERS = listOf(
            "record_type",
            "patient_id",
            "patient_fio",
            "date_of_birth",
            "gender",
            "phone",
            "email",
            "address_country",
            "address_city",
            "address_street",
            "address_building",
            "address_apartment",
            "address_postal_code",
            "insurance_number",
            "insurance_company",
            "insurance_valid_until",
            "medical_card_number",
            "registration_date",
            "history_id",
            "history_event_date",
            "history_doctor_id",
            "history_doctor_fio",
            "history_complaint",
            "history_diagnosis",
            "history_recommendation",
            "history_note",
            "appointment_id",
            "appointment_doctor_id",
            "appointment_doctor_fio",
            "appointment_date",
            "start_time",
            "end_time",
            "appointment_type",
            "appointment_status",
            "appointment_note"
        )
    }
}
