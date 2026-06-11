package ru.bagdasaryan.springkotlin.medkabinet.service

import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryComplaint
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryDiagnosis
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryRecommendation
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientMedicalHistoryEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientMedicalHistoryId
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.PatientMedicalHistoryPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindPatientMedicalHistoryQuery
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class PatientMedicalHistoryService(
    private val findPatientMedicalHistoryQuery: FindPatientMedicalHistoryQuery,
    private val patientMedicalHistoryPersister: PatientMedicalHistoryPersister
) {
    suspend fun findByPatientId(patientId: PatientId): Result<List<PatientMedicalHistoryEntity>> =
        findPatientMedicalHistoryQuery.execute(patientId)

    suspend fun findById(historyId: PatientMedicalHistoryId): Result<PatientMedicalHistoryEntity?> =
        findPatientMedicalHistoryQuery.findById(historyId)

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
    ): Result<PatientMedicalHistoryId> =
        patientMedicalHistoryPersister.createHistory(
            patientId = patientId,
            doctorId = doctorId,
            eventDate = eventDate,
            complaint = complaint,
            diagnosis = diagnosis,
            recommendation = recommendation,
            note = note,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    suspend fun updateHistory(
        id: PatientMedicalHistoryId,
        eventDate: LocalDate,
        complaint: MedicalHistoryComplaint,
        diagnosis: MedicalHistoryDiagnosis,
        recommendation: MedicalHistoryRecommendation,
        note: MedicalHistoryNote?,
        updatedAt: LocalDateTime
    ): Result<Unit> =
        patientMedicalHistoryPersister.updateHistory(
            id = id,
            eventDate = eventDate,
            complaint = complaint,
            diagnosis = diagnosis,
            recommendation = recommendation,
            note = note,
            updatedAt = updatedAt
        )
}
