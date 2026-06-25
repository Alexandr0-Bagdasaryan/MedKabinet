package ru.bagdasaryan.springkotlin.medkabinet.storage.persister.impls

import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryComplaint
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryDiagnosis
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryRecommendation
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientMedicalHistoryId
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.PatientMedicalHistoryPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.PatientMedicalHistories
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class PatientMedicalHistoryPersisterImpl : PatientMedicalHistoryPersister {
    override suspend fun createHistory(
        patientId: PatientId,
        doctorId: DoctorId,
        eventDate: LocalDate,
        complaint: MedicalHistoryComplaint,
        diagnosis: MedicalHistoryDiagnosis,
        recommendation: MedicalHistoryRecommendation,
        note: MedicalHistoryNote?,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime
    ): Result<PatientMedicalHistoryId> = runCatching {
        val newId = newSuspendedTransaction {
            PatientMedicalHistories.insertAndGetId {
                it[PatientMedicalHistories.patientId] = patientId.value
                it[PatientMedicalHistories.doctorId] = doctorId.value
                it[PatientMedicalHistories.eventDate] = eventDate
                it[PatientMedicalHistories.complaint] = complaint.value
                it[PatientMedicalHistories.diagnosis] = diagnosis.value
                it[PatientMedicalHistories.recommendation] = recommendation.value
                it[PatientMedicalHistories.note] = note?.value
                it[PatientMedicalHistories.createdAt] = createdAt
                it[PatientMedicalHistories.updatedAt] = updatedAt
            }
        }
        PatientMedicalHistoryId.create(newId.value).getOrThrow()
    }

    override suspend fun updateHistory(
        id: PatientMedicalHistoryId,
        eventDate: LocalDate,
        complaint: MedicalHistoryComplaint,
        diagnosis: MedicalHistoryDiagnosis,
        recommendation: MedicalHistoryRecommendation,
        note: MedicalHistoryNote?,
        updatedAt: LocalDateTime
    ): Result<Unit> = runCatching {
        val updated = newSuspendedTransaction {
            PatientMedicalHistories.update({ PatientMedicalHistories.id eq id.value }) {
                it[PatientMedicalHistories.eventDate] = eventDate
                it[PatientMedicalHistories.complaint] = complaint.value
                it[PatientMedicalHistories.diagnosis] = diagnosis.value
                it[PatientMedicalHistories.recommendation] = recommendation.value
                it[PatientMedicalHistories.note] = note?.value
                it[PatientMedicalHistories.updatedAt] = updatedAt
            }
        }
        require(updated > 0) { "Не удалось обновить историю болезни" }
    }
}
