package ru.bagdasaryan.springkotlin.medkabinet.storage.repository.impls

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Patients
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.PatientRepository

@Repository
class PatientRepositoryImpl : PatientRepository {
    override suspend fun getAll(): Result<List<PatientRepository.PatientDTO>> = runCatching {
        newSuspendedTransaction {
            Patients.selectAll()
                .orderBy(Patients.surname to SortOrder.ASC)
                .limit(500)
                .map { row ->
                    PatientRepository.PatientDTO(
                        id = row[Patients.id].value.toString(),
                        fio = listOf(row[Patients.surname], row[Patients.name], row[Patients.patronymic] ?: "")
                            .joinToString(" ")
                            .trim(),
                        cardNumber = row[Patients.medicalCardNumber]
                    )
                }
        }
    }
}
