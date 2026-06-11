package ru.bagdasaryan.springkotlin.medkabinet.storage.persister

import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryComplaint
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryDiagnosis
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryRecommendation
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientMedicalHistoryId
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface PatientMedicalHistoryPersister {
    suspend fun createHistory(
        patientId: PatientId,
        doctorId: DoctorId,
        eventDate: LocalDate,
        complaint: MedicalHistoryComplaint,
        diagnosis: MedicalHistoryDiagnosis,
        recommendation: MedicalHistoryRecommendation,
        note: MedicalHistoryNote?,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime
    ): Result<PatientMedicalHistoryId>

    suspend fun updateHistory(
        id: PatientMedicalHistoryId,
        eventDate: LocalDate,
        complaint: MedicalHistoryComplaint,
        diagnosis: MedicalHistoryDiagnosis,
        recommendation: MedicalHistoryRecommendation,
        note: MedicalHistoryNote?,
        updatedAt: LocalDateTime
    ): Result<Unit>
}
