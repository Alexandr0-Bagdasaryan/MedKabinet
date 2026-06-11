package ru.bagdasaryan.springkotlin.medkabinet.domain

import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.AddressPostalCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.MedicalCardNumber
import java.time.LocalDate
import java.time.LocalDateTime

data class PatientEntity(
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

