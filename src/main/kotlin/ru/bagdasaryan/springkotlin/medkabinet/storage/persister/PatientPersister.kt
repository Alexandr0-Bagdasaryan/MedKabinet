package ru.bagdasaryan.springkotlin.medkabinet.storage.persister

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
interface PatientPersister {
    suspend fun createPatient(
        fio: Fio,
        dateOfBirth: LocalDate,
        gender: PatientGender,
        phone: PhoneNumber,
        email: PatientEmail?,
        addressCountry: CountryCode,
        addressCity: AddressCity,
        addressStreet: AddressStreet,
        addressBuilding: AddressBuilding,
        addressApartment: AddressApartment?,
        addressPostalCode: AddressPostalCode?,
        insuranceNumber: InsuranceNumber?,
        insuranceCompany: InsuranceCompany?,
        insuranceValidUntil: LocalDate?,
        medicalCardNumber: MedicalCardNumber,
        registrationDate: LocalDateTime,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime
    ): Result<PatientId>

    suspend fun updatePatient(
        id: PatientId,
        fio: Fio,
        dateOfBirth: LocalDate,
        gender: PatientGender,
        phone: PhoneNumber,
        email: PatientEmail?,
        addressCountry: CountryCode,
        addressCity: AddressCity,
        addressStreet: AddressStreet,
        addressBuilding: AddressBuilding,
        addressApartment: AddressApartment?,
        addressPostalCode: AddressPostalCode?,
        insuranceNumber: InsuranceNumber?,
        insuranceCompany: InsuranceCompany?,
        insuranceValidUntil: LocalDate?,
        medicalCardNumber: MedicalCardNumber,
        updatedAt: LocalDateTime
    ): Result<Unit>
}
