package ru.bagdasaryan.springkotlin.medkabinet.storage.repository

import org.springframework.stereotype.Repository

@Repository
interface PatientRepository {
    suspend fun getAll(): Result<List<PatientDTO>>

    class PatientDTO(
        val id: String,
        val fio: String,
        val cardNumber: String
    )
}
