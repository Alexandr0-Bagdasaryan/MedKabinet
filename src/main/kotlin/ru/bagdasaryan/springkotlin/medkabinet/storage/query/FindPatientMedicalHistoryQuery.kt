package ru.bagdasaryan.springkotlin.medkabinet.storage.query

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryComplaint
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryDiagnosis
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryNote
import ru.bagdasaryan.springkotlin.medkabinet.domain.MedicalHistoryRecommendation
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientMedicalHistoryEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientMedicalHistoryId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.PatientMedicalHistories

@Component
class FindPatientMedicalHistoryQuery {

    suspend fun findById(historyId: PatientMedicalHistoryId): Result<PatientMedicalHistoryEntity?> = runCatching {
        newSuspendedTransaction {
            PatientMedicalHistories
                .join(Doctors, JoinType.INNER, PatientMedicalHistories.doctorId, Doctors.id)
                .selectAll()
                .where { PatientMedicalHistories.id eq historyId.value }
                .limit(1)
                .singleOrNull()
                ?.toEntity()
        }
    }

    suspend fun execute(patientId: PatientId): Result<List<PatientMedicalHistoryEntity>> = runCatching {
        newSuspendedTransaction {
            PatientMedicalHistories
                .join(Doctors, JoinType.INNER, PatientMedicalHistories.doctorId, Doctors.id)
                .selectAll()
                .where { PatientMedicalHistories.patientId eq patientId.value }
                .orderBy(PatientMedicalHistories.eventDate to SortOrder.DESC)
                .orderBy(PatientMedicalHistories.createdAt to SortOrder.DESC)
                .map { it.toEntity() }
        }
    }

    private fun ResultRow.toEntity(): PatientMedicalHistoryEntity =
        PatientMedicalHistoryEntity(
            id = PatientMedicalHistoryId(this[PatientMedicalHistories.id].value),
            patientId = PatientId(this[PatientMedicalHistories.patientId].value),
            doctorId = DoctorId(this[PatientMedicalHistories.doctorId].value),
            doctorFio = Fio.fromParts(this[Doctors.surname], this[Doctors.name], this[Doctors.patronymic]),
            eventDate = this[PatientMedicalHistories.eventDate],
            complaint = MedicalHistoryComplaint(this[PatientMedicalHistories.complaint]),
            diagnosis = MedicalHistoryDiagnosis(this[PatientMedicalHistories.diagnosis]),
            recommendation = MedicalHistoryRecommendation(this[PatientMedicalHistories.recommendation]),
            note = this[PatientMedicalHistories.note]?.let(::MedicalHistoryNote),
            createdAt = this[PatientMedicalHistories.createdAt],
            updatedAt = this[PatientMedicalHistories.updatedAt]
        )

    private fun ResultRow.toNullableEntity(): PatientMedicalHistoryEntity? = toEntity()
}
