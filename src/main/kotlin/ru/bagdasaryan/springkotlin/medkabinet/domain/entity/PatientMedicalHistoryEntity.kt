package ru.bagdasaryan.springkotlin.medkabinet.domain

import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import java.time.LocalDate
import java.time.LocalDateTime

data class PatientMedicalHistoryEntity(
    val id: PatientMedicalHistoryId,
    val patientId: PatientId,
    val doctorId: DoctorId,
    val doctorFio: Fio,
    val eventDate: LocalDate,
    val complaint: MedicalHistoryComplaint,
    val diagnosis: MedicalHistoryDiagnosis,
    val recommendation: MedicalHistoryRecommendation,
    val note: MedicalHistoryNote?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
