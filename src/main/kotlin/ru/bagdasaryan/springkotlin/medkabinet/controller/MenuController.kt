package ru.bagdasaryan.springkotlin.medkabinet.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.AuthPageHandler
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.AppointmentsPageHandler
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.DashboardPageHandler
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.DoctorsPageHandler
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.PatientPageHandler
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.PatientFormData
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.SchedulePageHandler
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.DoctorFormData
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressApartment
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressBuilding
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressCity
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressStreet
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentDurationMinutes
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentType
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryComplaint
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryDiagnosis
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryRecommendation
import ru.bagdasaryan.springkotlin.medkabinet.domain.CountryCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorEmail
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorLicenseNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.InsuranceCompany
import ru.bagdasaryan.springkotlin.medkabinet.domain.InsuranceNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientEmail
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientGender
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientMedicalHistoryEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientMedicalHistoryId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PhoneNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationId
import ru.bagdasaryan.springkotlin.medkabinet.domain.TimeSlotId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.AddressPostalCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.DoctorActivityStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.MedicalCardNumber
import ru.bagdasaryan.springkotlin.medkabinet.security.SecurityScopeService
import ru.bagdasaryan.springkotlin.medkabinet.service.PatientMedicalHistoryService
import ru.bagdasaryan.springkotlin.medkabinet.service.AppointmentService
import ru.bagdasaryan.springkotlin.medkabinet.service.DepartmentService
import ru.bagdasaryan.springkotlin.medkabinet.service.DoctorService
import ru.bagdasaryan.springkotlin.medkabinet.service.PatientService
import ru.bagdasaryan.springkotlin.medkabinet.service.SpecializationService
import java.time.LocalDate
import java.time.LocalDateTime
import java.nio.charset.StandardCharsets
import ru.bagdasaryan.springkotlin.medkabinet.domain.UserRole
import ru.bagdasaryan.springkotlin.medkabinet.security.AuthUserPrincipal
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.MedicalHistoryFormData

@RestController
class MenuController(
    private val schedulePageHandler: SchedulePageHandler,
    private val doctorsPageHandler: DoctorsPageHandler,
    private val dashboardPageHandler: DashboardPageHandler,
    private val appointmentsPageHandler: AppointmentsPageHandler,
    private val patientPageHandler: PatientPageHandler,
    private val authPageHandler: AuthPageHandler,
    private val appointmentService: AppointmentService,
    private val doctorService: DoctorService,
    private val departmentService: DepartmentService,
    private val specializationService: SpecializationService,
    private val patientService: PatientService,
    private val securityScopeService: SecurityScopeService,
    private val patientMedicalHistoryService: PatientMedicalHistoryService
) {
    private val pageSize = 30

    @GetMapping("/schedule", produces = ["text/html; charset=UTF-8"])
    suspend fun schedulePage(
        @RequestParam(name = "date", required = false) date: LocalDate?,
        @RequestParam(name = "doctorId", required = false) doctorId: Int?
    ) = ResponseEntity.ok(
        schedulePageHandler.renderPage(
            date = date,
            doctorId = doctorId,
            scopeDoctorId = securityScopeService.currentDoctorId()
        )
    )

    @GetMapping("/schedule/fragment", produces = ["text/html; charset=UTF-8"])
    suspend fun scheduleFragment(
        @RequestParam(name = "date", required = false) date: LocalDate?,
        @RequestParam(name = "doctorId", required = false) doctorId: Int?
    ) = ResponseEntity.ok(
        schedulePageHandler.renderFragment(
            date = date,
            doctorId = doctorId,
            scopeDoctorId = securityScopeService.currentDoctorId()
        )
    )

    @GetMapping("/", produces = ["text/html; charset=UTF-8"])
    suspend fun homePage() = ResponseEntity.ok(
        dashboardPageHandler.renderPage(securityScopeService.currentDoctorId())
    )

    @GetMapping("/doctors", produces = ["text/html; charset=UTF-8"])
    suspend fun doctorsPage(
        @RequestParam(name = "q", required = false) q: String?,
        @RequestParam(name = "page", required = false, defaultValue = "1") page: Int
    ): ResponseEntity<String> {
        securityScopeService.currentDoctorId()?.let { doctorId ->
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/doctors/${doctorId.value}")
                .build()
        }
        return ResponseEntity.ok(doctorsPageHandler.renderPage(q, page))
    }

    @GetMapping("/doctors/search", produces = ["text/html; charset=UTF-8"])
    suspend fun doctorsSearch(@RequestParam(name = "q", required = false) q: String?): ResponseEntity<String> {
        securityScopeService.currentDoctorId()?.let { doctorId ->
            val doctor = doctorService.findById(doctorId).getOrThrow()
                ?: throw NoSuchElementException("Не найден врач с id ${doctorId.value}")
            val matches = q.isNullOrBlank() || doctor.fio.value.contains(q.trim(), ignoreCase = true)
            return ResponseEntity.ok(if (matches) doctorsPageHandler.renderRows(doctor.fio.value) else "")
        }
        return ResponseEntity.ok(doctorsPageHandler.renderRows(q))
    }

    @GetMapping("/doctors/{id}", produces = ["text/html; charset=UTF-8"])
    suspend fun doctorPage(@PathVariable id: Int): ResponseEntity<Any> {
        val doctorId = DoctorId(id)
        if (!canAccessDoctor(doctorId)) {
            return forbiddenPage("Вы можете просматривать только свою карточку врача")
        }
        val doctor = doctorService.findById(doctorId).getOrThrow()
            ?: throw NoSuchElementException("Не найден врач с id $id")
        val specialization = specializationService.findById(doctor.specializationId).getOrThrow()
        val department = departmentService.findById(doctor.departmentId).getOrThrow()
        return ResponseEntity.ok(
            doctorsPageHandler.renderCardPage(
                doctor = doctor,
                specializationName = specialization?.name?.value ?: "-",
                departmentName = department?.name?.value ?: "-",
                departmentRoom = department?.roomNumber?.value,
                departmentPhone = department?.phone?.value
            )
        )
    }

    @GetMapping("/doctors/{id}/edit", produces = ["text/html; charset=UTF-8"])
    suspend fun editDoctorPage(@PathVariable id: Int): ResponseEntity<String> {
        val doctorId = DoctorId(id)
        val doctor = doctorService.findById(doctorId).getOrThrow()
            ?: throw NoSuchElementException("Не найден врач с id $id")
        return ResponseEntity.ok(
            doctorsPageHandler.renderEditPage(
                doctor = doctor,
                departments = departmentService.findAll().getOrThrow(),
                specializations = specializationService.findAll().getOrThrow()
            )
        )
    }

    @PostMapping("/doctors/{id}/edit")
    suspend fun updateDoctor(
        @PathVariable id: Int,
        @RequestParam fio: String,
        @RequestParam specializationId: String,
        @RequestParam departmentId: String,
        @RequestParam licenseNumber: String,
        @RequestParam licenseValidUntil: String,
        @RequestParam(required = false) phone: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam appointmentDurationMinutes: String,
        @RequestParam isActive: String
    ): ResponseEntity<Any> {
        val doctorId = DoctorId(id)
        val formData = DoctorFormData(
            fio = fio,
            specializationId = specializationId,
            departmentId = departmentId,
            licenseNumber = licenseNumber,
            licenseValidUntil = licenseValidUntil,
            phone = phone.orEmpty(),
            email = email.orEmpty(),
            appointmentDurationMinutes = appointmentDurationMinutes,
            isActive = isActive
        )

        val validation = validateDoctorForm(formData)
        if (validation.fieldErrors.isNotEmpty()) {
            val doctor = doctorService.findById(doctorId).getOrThrow()
                ?: throw NoSuchElementException("Не найден врач с id $id")
            return ResponseEntity.ok(
                doctorsPageHandler.renderEditPage(
                    doctor = doctor,
                    departments = departmentService.findAll().getOrThrow(),
                    specializations = specializationService.findAll().getOrThrow(),
                    formData = formData,
                    fieldErrors = validation.fieldErrors
                )
            )
        }

        val values = validation.values!!
        val result = runCatching {
            doctorService.updateDoctor(
                id = doctorId,
                fio = values.fio,
                specializationId = values.specializationId,
                departmentId = values.departmentId,
                licenseNumber = values.licenseNumber,
                licenseValidUntil = values.licenseValidUntil,
                phone = values.phone,
                email = values.email,
                appointmentDurationMinutes = values.appointmentDurationMinutes,
                isActive = values.isActive
            ).getOrThrow()
        }

        if (result.isFailure) {
            val message = result.exceptionOrNull()?.message ?: "Ошибка при обновлении врача"
            val doctor = doctorService.findById(doctorId).getOrThrow()
                ?: throw NoSuchElementException("Не найден врач с id $id")
            return ResponseEntity.ok(
                doctorsPageHandler.renderEditPage(
                    doctor = doctor,
                    departments = departmentService.findAll().getOrThrow(),
                    specializations = specializationService.findAll().getOrThrow(),
                    formData = formData,
                    fieldErrors = mapOf("_form" to message)
                )
            )
        }

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header(HttpHeaders.LOCATION, "/doctors/$id")
            .build()
    }

    @GetMapping("/patients/{id}", produces = ["text/html; charset=UTF-8"])
    suspend fun patientPage(
        @PathVariable id: Int
    ): ResponseEntity<Any> {
        val patientId = PatientId.create(id).getOrThrow()
        if (!canAccessPatient(patientId)) {
            return forbiddenPage("Вы можете просматривать только собственную карточку пациента")
        }
        val patient = patientService.findById(patientId).getOrThrow()
            ?: throw NoSuchElementException("Не найден пациент с id $id")
        val history = patientMedicalHistoryService.findByPatientId(patientId).getOrThrow()
        val editableHistoryIds = editableHistoryIds(history)
        return ResponseEntity.ok(
            patientPageHandler.renderCardPage(
                patient = patient,
                medicalHistory = history,
                editableHistoryIds = editableHistoryIds,
                canEditPatient = securityScopeService.isAdmin() || securityScopeService.isPatient(),
                canCreatePatient = securityScopeService.isAdmin(),
                canBookAppointment = securityScopeService.isAdmin() || securityScopeService.isDoctor(),
                canCreateMedicalHistory = securityScopeService.isDoctor()
            )
        )
    }

    @GetMapping("/patients", produces = ["text/html; charset=UTF-8"])
    suspend fun patientsPage(
        @RequestParam(name = "page", required = false, defaultValue = "1") page: Int,
        @RequestParam(name = "error", required = false) error: String?
    ): ResponseEntity<String> {
        val patients = securityScopeService.currentDoctorId()?.let { doctorId ->
            patientService.findAllByDoctor(doctorId).getOrThrow()
        } ?: patientService.findAll().getOrThrow()

        return ResponseEntity.ok(
            patientPageHandler.renderListPage(
                patients = patients,
                page = page,
                errorMessage = error,
                canCreatePatient = securityScopeService.isAdmin(),
                canEditPatient = securityScopeService.isAdmin()
            )
        )
    }

    @GetMapping("/patients/{patientId}/history/{historyId}/edit", produces = ["text/html; charset=UTF-8"])
    suspend fun editMedicalHistoryPage(
        @PathVariable patientId: Int,
        @PathVariable historyId: Int
    ): ResponseEntity<Any> {
        val patientVo = PatientId.create(patientId).getOrThrow()
        val historyVo = PatientMedicalHistoryId.create(historyId).getOrThrow()
        val history = patientMedicalHistoryService.findById(historyVo).getOrThrow()
            ?: throw NoSuchElementException("Не найдена запись истории болезни с id $historyId")
        if (history.patientId != patientVo) {
            throw NoSuchElementException("Не найдена запись истории болезни с id $historyId")
        }
        if (!canEditMedicalHistory(history)) {
            return forbiddenPage("Вы можете редактировать только собственные записи истории болезни")
        }
        val patient = patientService.findById(patientVo).getOrThrow()
            ?: throw NoSuchElementException("Не найден пациент с id $patientId")
        return ResponseEntity.ok(
            patientPageHandler.renderHistoryEditPage(
                patient = patient,
                history = history
            )
        )
    }

    @GetMapping("/patients/{patientId}/history/new", produces = ["text/html; charset=UTF-8"])
    suspend fun newMedicalHistoryPage(
        @PathVariable patientId: Int
    ): ResponseEntity<Any> {
        val patientVo = PatientId.create(patientId).getOrThrow()
        if (!canCreateMedicalHistory(patientVo)) {
            return forbiddenPage("Вы можете добавлять записи только в историю своих пациентов")
        }
        val patient = patientService.findById(patientVo).getOrThrow()
            ?: throw NoSuchElementException("Не найден пациент с id $patientId")
        return ResponseEntity.ok(
            patientPageHandler.renderHistoryCreatePage(patient = patient)
        )
    }

    @PostMapping("/patients/{patientId}/history")
    suspend fun createMedicalHistory(
        @PathVariable patientId: Int,
        @RequestParam eventDate: String,
        @RequestParam complaint: String,
        @RequestParam diagnosis: String,
        @RequestParam recommendation: String,
        @RequestParam(required = false) note: String?
    ): ResponseEntity<Any> {
        val patientVo = PatientId.create(patientId).getOrThrow()
        if (!canCreateMedicalHistory(patientVo)) {
            return forbiddenPage("Вы можете добавлять записи только в историю своих пациентов")
        }

        val formData = MedicalHistoryFormData(
            eventDate = eventDate,
            complaint = complaint,
            diagnosis = diagnosis,
            recommendation = recommendation,
            note = note.orEmpty()
        )

        val validation = validateMedicalHistoryForm(formData)
        if (validation.fieldErrors.isNotEmpty()) {
            val patient = patientService.findById(patientVo).getOrThrow()
                ?: throw NoSuchElementException("Не найден пациент с id $patientId")
            return ResponseEntity.ok(
                patientPageHandler.renderHistoryCreatePage(
                    patient = patient,
                    formData = formData,
                    fieldErrors = validation.fieldErrors
                )
            )
        }

        val doctorId = securityScopeService.currentDoctorId()
            ?: return forbiddenPage("Добавление записи в историю болезни доступно только врачу")

        val values = validation.values!!
        val result = runCatching {
            val now = LocalDateTime.now()
            patientMedicalHistoryService.createHistory(
                patientId = patientVo,
                doctorId = doctorId,
                eventDate = values.eventDate,
                complaint = values.complaint,
                diagnosis = values.diagnosis,
                recommendation = values.recommendation,
                note = values.note,
                createdAt = now,
                updatedAt = now
            ).getOrThrow()
        }

        if (result.isFailure) {
            val message = result.exceptionOrNull()?.message ?: "Ошибка при добавлении записи в историю болезни"
            val patient = patientService.findById(patientVo).getOrThrow()
                ?: throw NoSuchElementException("Не найден пациент с id $patientId")
            return ResponseEntity.ok(
                patientPageHandler.renderHistoryCreatePage(
                    patient = patient,
                    formData = formData,
                    fieldErrors = mapOf("_form" to message)
                )
            )
        }

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header(HttpHeaders.LOCATION, "/patients/$patientId")
            .build()
    }

    @PostMapping("/patients/{patientId}/history/{historyId}/edit")
    suspend fun updateMedicalHistory(
        @PathVariable patientId: Int,
        @PathVariable historyId: Int,
        @RequestParam eventDate: String,
        @RequestParam complaint: String,
        @RequestParam diagnosis: String,
        @RequestParam recommendation: String,
        @RequestParam(required = false) note: String?
    ): ResponseEntity<Any> {
        val patientVo = PatientId.create(patientId).getOrThrow()
        val historyVo = PatientMedicalHistoryId.create(historyId).getOrThrow()
        val history = patientMedicalHistoryService.findById(historyVo).getOrThrow()
            ?: throw NoSuchElementException("Не найдена запись истории болезни с id $historyId")
        if (history.patientId != patientVo) {
            throw NoSuchElementException("Не найдена запись истории болезни с id $historyId")
        }
        if (!canEditMedicalHistory(history)) {
            return forbiddenPage("Вы можете редактировать только собственные записи истории болезни")
        }

        val formData = MedicalHistoryFormData(
            eventDate = eventDate,
            complaint = complaint,
            diagnosis = diagnosis,
            recommendation = recommendation,
            note = note.orEmpty()
        )

        val validation = validateMedicalHistoryForm(formData)
        if (validation.fieldErrors.isNotEmpty()) {
            val patient = patientService.findById(patientVo).getOrThrow()
                ?: throw NoSuchElementException("Не найден пациент с id $patientId")
            return ResponseEntity.ok(
                patientPageHandler.renderHistoryEditPage(
                    patient = patient,
                    history = history,
                    formData = formData,
                    fieldErrors = validation.fieldErrors
                )
            )
        }

        val values = validation.values!!
        val result = runCatching {
            patientMedicalHistoryService.updateHistory(
                id = historyVo,
                eventDate = values.eventDate,
                complaint = values.complaint,
                diagnosis = values.diagnosis,
                recommendation = values.recommendation,
                note = values.note,
                updatedAt = LocalDateTime.now()
            ).getOrThrow()
        }

        if (result.isFailure) {
            val message = result.exceptionOrNull()?.message ?: "Ошибка при обновлении истории болезни"
            val patient = patientService.findById(patientVo).getOrThrow()
                ?: throw NoSuchElementException("Не найден пациент с id $patientId")
            return ResponseEntity.ok(
                patientPageHandler.renderHistoryEditPage(
                    patient = patient,
                    history = history,
                    formData = formData,
                    fieldErrors = mapOf("_form" to message)
                )
            )
        }

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header(HttpHeaders.LOCATION, "/patients/$patientId")
            .build()
    }

    @GetMapping("/patients/new", produces = ["text/html; charset=UTF-8"])
    suspend fun newPatientPage(
        @RequestParam(name = "error", required = false) error: String?
    ): ResponseEntity<String> =
        ResponseEntity.ok(
            patientPageHandler.renderCreatePage(
                fieldErrors = error?.let { mapOf("_form" to it) } ?: emptyMap()
            )
        )

    @GetMapping("/patients/{id}/edit", produces = ["text/html; charset=UTF-8"])
    suspend fun editPatientPage(
        @PathVariable id: Int,
        @RequestParam(name = "error", required = false) error: String?
    ): ResponseEntity<Any> {
        val patientId = PatientId.create(id).getOrThrow()
        if (!canAccessPatient(patientId)) {
            return forbiddenPage("Вы можете редактировать только собственную карточку пациента")
        }
        val patient = patientService.findById(patientId).getOrThrow()
            ?: throw NoSuchElementException("Не найден пациент с id $id")
        return ResponseEntity.ok(
            patientPageHandler.renderEditPage(
                patient = patient,
                fieldErrors = error?.let { mapOf("_form" to it) } ?: emptyMap()
            )
        )
    }

    @PostMapping("/patients")
    suspend fun createPatient(
        @RequestParam fio: String,
        @RequestParam dateOfBirth: String,
        @RequestParam gender: String,
        @RequestParam phone: String,
        @RequestParam(required = false) email: String?,
        @RequestParam addressCountry: String,
        @RequestParam addressCity: String,
        @RequestParam addressStreet: String,
        @RequestParam addressBuilding: String,
        @RequestParam(required = false) addressApartment: String?,
        @RequestParam(required = false) addressPostalCode: String?,
        @RequestParam(required = false) insuranceNumber: String?,
        @RequestParam(required = false) insuranceCompany: String?,
        @RequestParam(required = false) insuranceValidUntil: String?,
        @RequestParam medicalCardNumber: String
    ): ResponseEntity<Any> {
        val formData = PatientFormData(
            fio = fio,
            dateOfBirth = dateOfBirth,
            gender = gender,
            phone = phone,
            email = email.orEmpty(),
            addressCountry = addressCountry,
            addressCity = addressCity,
            addressStreet = addressStreet,
            addressBuilding = addressBuilding,
            addressApartment = addressApartment.orEmpty(),
            addressPostalCode = addressPostalCode.orEmpty(),
            insuranceNumber = insuranceNumber.orEmpty(),
            insuranceCompany = insuranceCompany.orEmpty(),
            insuranceValidUntil = insuranceValidUntil.orEmpty(),
            medicalCardNumber = medicalCardNumber
        )

        val validation = validatePatientForm(formData)
        if (validation.fieldErrors.isNotEmpty()) {
            return ResponseEntity.ok(
                patientPageHandler.renderCreatePage(
                    formData = formData,
                    fieldErrors = validation.fieldErrors
                )
            )
        }

        val values = validation.values!!
        val result = runCatching {
            patientService.createPatient(
                fio = values.fio,
                dateOfBirth = values.dateOfBirth,
                gender = values.gender,
                phone = values.phone,
                email = values.email,
                addressCountry = values.addressCountry,
                addressCity = values.addressCity,
                addressStreet = values.addressStreet,
                addressBuilding = values.addressBuilding,
                addressApartment = values.addressApartment,
                addressPostalCode = values.addressPostalCode,
                insuranceNumber = values.insuranceNumber,
                insuranceCompany = values.insuranceCompany,
                insuranceValidUntil = values.insuranceValidUntil,
                medicalCardNumber = values.medicalCardNumber
            ).getOrThrow()
        }

        if (result.isFailure) {
            val message = result.exceptionOrNull()?.message ?: "Ошибка при создании пациента"
            return ResponseEntity.ok(
                patientPageHandler.renderCreatePage(
                    formData = formData,
                    fieldErrors = mapOf("_form" to message)
                )
            )
        }

        val patientId = result.getOrThrow()
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header(HttpHeaders.LOCATION, "/patients/${patientId.value}")
            .build()
    }

    @PostMapping("/patients/{id}/edit")
    suspend fun updatePatient(
        @PathVariable id: Int,
        @RequestParam fio: String,
        @RequestParam dateOfBirth: String,
        @RequestParam gender: String,
        @RequestParam phone: String,
        @RequestParam(required = false) email: String?,
        @RequestParam addressCountry: String,
        @RequestParam addressCity: String,
        @RequestParam addressStreet: String,
        @RequestParam addressBuilding: String,
        @RequestParam(required = false) addressApartment: String?,
        @RequestParam(required = false) addressPostalCode: String?,
        @RequestParam(required = false) insuranceNumber: String?,
        @RequestParam(required = false) insuranceCompany: String?,
        @RequestParam(required = false) insuranceValidUntil: String?,
        @RequestParam medicalCardNumber: String
    ): ResponseEntity<Any> {
        val patientId = PatientId.create(id).getOrThrow()
        if (!canAccessPatient(patientId)) {
            return forbiddenPage("Вы можете изменять только собственную карточку пациента")
        }

        val formData = PatientFormData(
            fio = fio,
            dateOfBirth = dateOfBirth,
            gender = gender,
            phone = phone,
            email = email.orEmpty(),
            addressCountry = addressCountry,
            addressCity = addressCity,
            addressStreet = addressStreet,
            addressBuilding = addressBuilding,
            addressApartment = addressApartment.orEmpty(),
            addressPostalCode = addressPostalCode.orEmpty(),
            insuranceNumber = insuranceNumber.orEmpty(),
            insuranceCompany = insuranceCompany.orEmpty(),
            insuranceValidUntil = insuranceValidUntil.orEmpty(),
            medicalCardNumber = medicalCardNumber
        )

        val validation = validatePatientForm(formData)
        if (validation.fieldErrors.isNotEmpty()) {
            val patient = patientService.findById(patientId).getOrThrow()
                ?: throw NoSuchElementException("Не найден пациент с id $id")
            return ResponseEntity.ok(
                patientPageHandler.renderEditPage(
                    patient = patient,
                    formData = formData,
                    fieldErrors = validation.fieldErrors
                )
            )
        }

        val values = validation.values!!
        val result = runCatching {
            patientService.updatePatient(
                id = patientId,
                fio = values.fio,
                dateOfBirth = values.dateOfBirth,
                gender = values.gender,
                phone = values.phone,
                email = values.email,
                addressCountry = values.addressCountry,
                addressCity = values.addressCity,
                addressStreet = values.addressStreet,
                addressBuilding = values.addressBuilding,
                addressApartment = values.addressApartment,
                addressPostalCode = values.addressPostalCode,
                insuranceNumber = values.insuranceNumber,
                insuranceCompany = values.insuranceCompany,
                insuranceValidUntil = values.insuranceValidUntil,
                medicalCardNumber = values.medicalCardNumber
            ).getOrThrow()
        }

        if (result.isFailure) {
            val message = result.exceptionOrNull()?.message ?: "Ошибка при обновлении пациента"
            return ResponseEntity.ok(
                patientPageHandler.renderEditPage(
                    patient = patientService.findById(patientId).getOrThrow()
                        ?: throw NoSuchElementException("Не найден пациент с id $id"),
                    formData = formData,
                    fieldErrors = mapOf("_form" to message)
                )
            )
        }

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header(HttpHeaders.LOCATION, "/patients/${id}")
            .build()
    }

    @GetMapping("/appointments", produces = ["text/html; charset=UTF-8"])
    suspend fun appointmentsPage(
        @RequestParam(name = "page", required = false, defaultValue = "1") page: Int,
        @RequestParam(name = "error", required = false) error: String?
    ): ResponseEntity<String> {
        val safePage = page.coerceAtLeast(1)
        val doctorScopeId = securityScopeService.currentDoctorId()
        val appointmentsPage = appointmentService.findAppointments(safePage, pageSize, doctorScopeId).getOrThrow()
        val slots = appointmentService.findAvailableSlots(doctorScopeId).getOrThrow()
        val patients = doctorScopeId?.let { patientService.findAllByDoctor(it).getOrThrow() }
            ?: patientService.findAll().getOrThrow()

        return ResponseEntity.ok(
            appointmentsPageHandler.renderPage(
                appointments = appointmentsPage.rows,
                patients = patients,
                slots = slots,
                page = safePage,
                hasPrev = safePage > 1,
                hasNext = appointmentsPage.hasNext,
                errorMessage = error,
                doctorScope = doctorScopeId != null
            )
        )
    }

    @PostMapping("/appointments")
    suspend fun createAppointment(
        @RequestParam patientId: Int,
        @RequestParam timeSlotId: Int,
        @RequestParam appointmentType: String,
        @RequestParam appointmentStatus: String,
        @RequestParam(required = false) notes: String?
    ): ResponseEntity<Void> {
        val result = runCatching {
            val patientIdVo = PatientId.create(patientId).getOrThrow()
            val timeSlotIdVo = TimeSlotId.create(timeSlotId).getOrThrow()
            val appointmentTypeVo = AppointmentType.create(appointmentType.trim()).getOrThrow()
            val appointmentStatusVo = AppointmentStatus.create(appointmentStatus.trim()).getOrThrow()
            val noteVo = notes?.trim()?.takeIf { it.isNotBlank() }?.let { AppointmentNote.create(it).getOrThrow() }

            appointmentService.createAppointment(
                patientId = patientIdVo,
                timeSlotId = timeSlotIdVo,
                appointmentType = appointmentTypeVo,
                appointmentStatus = appointmentStatusVo,
                notes = noteVo,
                expectedDoctorId = securityScopeService.currentDoctorId()
            ).getOrThrow()
        }

        if (result.isFailure) {
            val message = result.exceptionOrNull()?.message ?: "Ошибка при создании записи"
            val encoded = java.net.URLEncoder.encode(message, StandardCharsets.UTF_8)
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/appointments?error=$encoded")
                .build()
        }

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header(HttpHeaders.LOCATION, "/appointments")
            .build()
    }

    private data class ValidPatientValues(
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

    private data class PatientValidationResult(
        val values: ValidPatientValues?,
        val fieldErrors: Map<String, String>
    )

    private fun validatePatientForm(formData: PatientFormData): PatientValidationResult {
        val errors = linkedMapOf<String, String>()

        fun <T> required(field: String, block: () -> T): T? =
            runCatching(block).getOrElse {
                errors[field] = it.message ?: "Некорректное значение"
                null
            }

        fun <T> optional(field: String, raw: String, block: (String) -> T): T? {
            val trimmed = raw.trim()
            if (trimmed.isBlank()) return null
            return runCatching { block(trimmed) }.getOrElse {
                errors[field] = it.message ?: "Некорректное значение"
                null
            }
        }

        val fioVo = required("fio") { Fio.create(formData.fio).getOrThrow() }
        val dateOfBirthVo = required("dateOfBirth") {
            runCatching { LocalDate.parse(formData.dateOfBirth.trim()) }.getOrElse { error("Некорректная дата рождения") }
        }
        val genderVo = required("gender") { PatientGender.from(formData.gender) }
        val phoneVo = required("phone") { PhoneNumber(formData.phone) }
        val emailVo = optional("email", formData.email) { PatientEmail(it) }
        val countryVo = required("addressCountry") { CountryCode(formData.addressCountry.trim()) }
        val cityVo = required("addressCity") { AddressCity(formData.addressCity.trim()) }
        val streetVo = required("addressStreet") { AddressStreet(formData.addressStreet.trim()) }
        val buildingVo = required("addressBuilding") { AddressBuilding(formData.addressBuilding.trim()) }
        val apartmentVo = optional("addressApartment", formData.addressApartment) { AddressApartment(it) }
        val postalCodeVo = optional("addressPostalCode", formData.addressPostalCode) { AddressPostalCode(it) }
        val insuranceNumberVo = optional("insuranceNumber", formData.insuranceNumber) { InsuranceNumber(it) }
        val insuranceCompanyVo = optional("insuranceCompany", formData.insuranceCompany) { InsuranceCompany(it) }
        val insuranceValidUntilVo = optional("insuranceValidUntil", formData.insuranceValidUntil) {
            runCatching { LocalDate.parse(it) }.getOrElse { error("Некорректная дата окончания полиса") }
        }
        val medicalCardNumberVo = required("medicalCardNumber") { MedicalCardNumber(formData.medicalCardNumber) }

        val values = if (errors.isEmpty()) {
            ValidPatientValues(
                fio = fioVo!!,
                dateOfBirth = dateOfBirthVo!!,
                gender = genderVo!!,
                phone = phoneVo!!,
                email = emailVo,
                addressCountry = countryVo!!,
                addressCity = cityVo!!,
                addressStreet = streetVo!!,
                addressBuilding = buildingVo!!,
                addressApartment = apartmentVo,
                addressPostalCode = postalCodeVo,
                insuranceNumber = insuranceNumberVo,
                insuranceCompany = insuranceCompanyVo,
                insuranceValidUntil = insuranceValidUntilVo,
                medicalCardNumber = medicalCardNumberVo!!
            )
        } else {
            null
        }

        return PatientValidationResult(values = values, fieldErrors = errors)
    }

    private data class ValidDoctorValues(
        val fio: Fio,
        val specializationId: SpecializationId,
        val departmentId: DepartmentId,
        val licenseNumber: DoctorLicenseNumber,
        val licenseValidUntil: LocalDate,
        val phone: PhoneNumber?,
        val email: DoctorEmail?,
        val appointmentDurationMinutes: AppointmentDurationMinutes,
        val isActive: DoctorActivityStatus
    )

    private data class DoctorValidationResult(
        val values: ValidDoctorValues?,
        val fieldErrors: Map<String, String>
    )

    private data class ValidHistoryValues(
        val eventDate: LocalDate,
        val complaint: MedicalHistoryComplaint,
        val diagnosis: MedicalHistoryDiagnosis,
        val recommendation: MedicalHistoryRecommendation,
        val note: MedicalHistoryNote?
    )

    private data class MedicalHistoryValidationResult(
        val values: ValidHistoryValues?,
        val fieldErrors: Map<String, String>
    )

    private fun currentPrincipal(): AuthUserPrincipal? =
        securityScopeService.currentPrincipal()

    private fun canAccessDoctor(doctorId: DoctorId): Boolean {
        val principal = currentPrincipal() ?: return false
        return when (principal.role) {
            UserRole.ADMIN, UserRole.PATIENT -> true
            UserRole.DOCTOR -> principal.doctorId == doctorId
        }
    }

    private suspend fun canAccessPatient(patientId: PatientId): Boolean {
        val principal = currentPrincipal() ?: return false
        return when (principal.role) {
            UserRole.ADMIN -> true
            UserRole.DOCTOR -> principal.doctorId?.let { doctorId ->
                patientService.isAssignedToDoctor(doctorId, patientId).getOrElse { false }
            } ?: false
            UserRole.PATIENT -> principal.patientId == patientId
        }
    }

    private fun forbiddenPage(message: String): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(authPageHandler.renderAccessDeniedPage(message))

    private fun editableHistoryIds(history: List<PatientMedicalHistoryEntity>): Set<Int> {
        val principal = currentPrincipal() ?: return emptySet()
        return when (principal.role) {
            UserRole.ADMIN -> history.map { it.id.value }.toSet()
            UserRole.DOCTOR -> principal.doctorId?.let { doctorId ->
                history.filter { it.doctorId == doctorId }.map { it.id.value }.toSet()
            } ?: emptySet()
            UserRole.PATIENT -> emptySet()
        }
    }

    private fun canEditMedicalHistory(history: PatientMedicalHistoryEntity): Boolean {
        val principal = currentPrincipal() ?: return false
        return when (principal.role) {
            UserRole.ADMIN -> true
            UserRole.DOCTOR -> principal.doctorId == history.doctorId
            UserRole.PATIENT -> false
        }
    }

    private suspend fun canCreateMedicalHistory(patientId: PatientId): Boolean {
        val principal = currentPrincipal() ?: return false
        return when (principal.role) {
            UserRole.ADMIN -> false
            UserRole.DOCTOR -> principal.doctorId?.let { doctorId ->
                patientService.isAssignedToDoctor(doctorId, patientId).getOrElse { false }
            } ?: false
            UserRole.PATIENT -> false
        }
    }

    private fun validateMedicalHistoryForm(formData: MedicalHistoryFormData): MedicalHistoryValidationResult {
        val errors = linkedMapOf<String, String>()

        fun <T> required(field: String, block: () -> T): T? =
            runCatching(block).getOrElse {
                errors[field] = it.message ?: "Некорректное значение"
                null
            }

        fun <T> optional(field: String, raw: String, block: (String) -> T): T? {
            val trimmed = raw.trim()
            if (trimmed.isBlank()) return null
            return runCatching { block(trimmed) }.getOrElse {
                errors[field] = it.message ?: "Некорректное значение"
                null
            }
        }

        val eventDateVo = required("eventDate") {
            runCatching { LocalDate.parse(formData.eventDate.trim()) }.getOrElse { error("Некорректная дата приема") }
        }
        val complaintVo = required("complaint") { MedicalHistoryComplaint.create(formData.complaint).getOrThrow() }
        val diagnosisVo = required("diagnosis") { MedicalHistoryDiagnosis.create(formData.diagnosis).getOrThrow() }
        val recommendationVo = required("recommendation") { MedicalHistoryRecommendation.create(formData.recommendation).getOrThrow() }
        val noteVo = optional("note", formData.note) { MedicalHistoryNote.create(it).getOrThrow() }

        val values = if (errors.isEmpty()) {
            ValidHistoryValues(
                eventDate = eventDateVo!!,
                complaint = complaintVo!!,
                diagnosis = diagnosisVo!!,
                recommendation = recommendationVo!!,
                note = noteVo
            )
        } else {
            null
        }

        return MedicalHistoryValidationResult(values = values, fieldErrors = errors)
    }

    private suspend fun validateDoctorForm(formData: DoctorFormData): DoctorValidationResult {
        val errors = linkedMapOf<String, String>()

        fun <T> required(field: String, block: () -> T): T? =
            runCatching(block).getOrElse {
                errors[field] = it.message ?: "Некорректное значение"
                null
            }

        fun <T> optional(field: String, raw: String, block: (String) -> T): T? {
            val trimmed = raw.trim()
            if (trimmed.isBlank()) return null
            return runCatching { block(trimmed) }.getOrElse {
                errors[field] = it.message ?: "Некорректное значение"
                null
            }
        }

        val fioVo = required("fio") { Fio.create(formData.fio).getOrThrow() }
        val specializationIdVo = runCatching {
            val parsed = formData.specializationId.trim().toIntOrNull() ?: error("Некорректная специализация")
            val vo = SpecializationId(parsed)
            if (specializationService.findById(vo).getOrThrow() == null) error("Специализация не найдена")
            vo
        }.getOrElse {
            errors["specializationId"] = it.message ?: "Некорректное значение"
            null
        }
        val departmentIdVo = runCatching {
            val parsed = formData.departmentId.trim().toIntOrNull() ?: error("Некорректное отделение")
            val vo = DepartmentId(parsed)
            if (departmentService.findById(vo).getOrThrow() == null) error("Отделение не найдено")
            vo
        }.getOrElse {
            errors["departmentId"] = it.message ?: "Некорректное значение"
            null
        }
        val licenseNumberVo = required("licenseNumber") { DoctorLicenseNumber(formData.licenseNumber) }
        val licenseValidUntilVo = required("licenseValidUntil") { LocalDate.parse(formData.licenseValidUntil.trim()) }
        val phoneVo = optional("phone", formData.phone) { PhoneNumber(it) }
        val emailVo = optional("email", formData.email) { DoctorEmail(it) }
        val durationVo = required("appointmentDurationMinutes") {
            val parsed = formData.appointmentDurationMinutes.trim().toShortOrNull() ?: error("Некорректная длительность приема")
            AppointmentDurationMinutes(parsed)
        }
        val isActiveVo = required("isActive") { DoctorActivityStatus.from(formData.isActive) }

        val values = if (errors.isEmpty()) {
            ValidDoctorValues(
                fio = fioVo!!,
                specializationId = specializationIdVo!!,
                departmentId = departmentIdVo!!,
                licenseNumber = licenseNumberVo!!,
                licenseValidUntil = licenseValidUntilVo!!,
                phone = phoneVo,
                email = emailVo,
                appointmentDurationMinutes = durationVo!!,
                isActive = isActiveVo!!
            )
        } else {
            null
        }

        return DoctorValidationResult(values = values, fieldErrors = errors)
    }
}
