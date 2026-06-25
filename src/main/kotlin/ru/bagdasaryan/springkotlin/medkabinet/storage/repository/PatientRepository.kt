package ru.bagdasaryan.springkotlin.medkabinet.storage.repository

import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressApartment
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressBuilding
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressCity
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.AddressPostalCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressStreet
import ru.bagdasaryan.springkotlin.medkabinet.domain.CountryCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.InsuranceCompany
import ru.bagdasaryan.springkotlin.medkabinet.domain.InsuranceNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.MedicalCardNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientEmail
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientGender
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PhoneNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface PatientRepository {
    suspend fun getAll(): Result<List<PatientDTO>>
    suspend fun findById(id: PatientId): Result<PatientDTO?>
    suspend fun findByMedicalCardNumber(medicalCardNumber: MedicalCardNumber): Result<PatientDTO?>
    suspend fun existsById(id: PatientId): Result<Boolean>

    class PatientDTO(
        val id: PatientId,
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
        val medicalCardNumber: MedicalCardNumber,
        val registrationDate: LocalDateTime,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
    )
}
