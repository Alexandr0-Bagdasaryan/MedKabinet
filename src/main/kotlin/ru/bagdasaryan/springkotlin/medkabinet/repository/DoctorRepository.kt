package ru.bagdasaryan.springkotlin.medkabinet.repository

import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.vo.Fio

@Repository
interface DoctorRepository {
   suspend fun getAll() : Result<List<DoctorDTO>>

    suspend fun findByFio(fio: Fio): Result<List<DoctorDTO>>

    class DoctorDTO(
        val firstName: String,
        val lastName: String,
        val middleName: String,
        val specializationId: String,
        val departmentId: String,
        val licenseNumber: String,
        val licenseValidUntil: String,
        val phone: String,
        val email: String
    )
}