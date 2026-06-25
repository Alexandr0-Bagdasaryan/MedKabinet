package ru.bagdasaryan.springkotlin.medkabinet.storage.repository.impls

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
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
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Patients
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.PatientRepository

@Repository
class PatientRepositoryImpl : PatientRepository {
    override suspend fun existsById(id: PatientId): Result<Boolean> = runCatching {
        newSuspendedTransaction {
            Patients.selectAll().where { Patients.id eq id.value }.any()
        }
    }

    override suspend fun findById(id: PatientId): Result<PatientRepository.PatientDTO?> = runCatching {
        newSuspendedTransaction {
            Patients.selectAll()
                .where { Patients.id eq id.value }
                .singleOrNull()
                ?.toDTO()
        }
    }

    override suspend fun findByMedicalCardNumber(medicalCardNumber: MedicalCardNumber): Result<PatientRepository.PatientDTO?> = runCatching {
        newSuspendedTransaction {
            Patients.selectAll()
                .where { Patients.medicalCardNumber eq medicalCardNumber.value }
                .singleOrNull()
                ?.toDTO()
        }
    }

    override suspend fun getAll(): Result<List<PatientRepository.PatientDTO>> = runCatching {
        newSuspendedTransaction {
            Patients.selectAll()
                .orderBy(Patients.surname to SortOrder.ASC)
                .limit(500)
                .map { row ->
                    PatientRepository.PatientDTO(
                        id = PatientId(row[Patients.id].value),
                        fio = Fio.fromParts(row[Patients.surname], row[Patients.name], row[Patients.patronymic]),
                        dateOfBirth = row[Patients.dateOfBirth],
                        gender = PatientGender.from(row[Patients.gender]),
                        phone = PhoneNumber(row[Patients.phone]),
                        email = row[Patients.email]?.let { PatientEmail(it) },
                        addressCountry = CountryCode(row[Patients.addressCountry]),
                        addressCity = AddressCity(row[Patients.addressCity]),
                        addressStreet = AddressStreet(row[Patients.addressStreet]),
                        addressBuilding = AddressBuilding(row[Patients.addressBuilding]),
                        addressApartment = row[Patients.addressApartment]?.let { AddressApartment(it) },
                        addressPostalCode = row[Patients.addressPostalCode]?.let { AddressPostalCode(it) },
                        insuranceNumber = row[Patients.insuranceNumber]?.let { InsuranceNumber(it) },
                        insuranceCompany = row[Patients.insuranceCompany]?.let { InsuranceCompany(it) },
                        insuranceValidUntil = row[Patients.insuranceValidUntil],
                        medicalCardNumber = MedicalCardNumber(row[Patients.medicalCardNumber]),
                        registrationDate = row[Patients.registrationDate],
                        createdAt = row[Patients.createdAt],
                        updatedAt = row[Patients.updatedAt],
                        deletedAt = row[Patients.deletedAt]
                    )
                }
        }
    }

    private fun org.jetbrains.exposed.sql.ResultRow.toDTO(): PatientRepository.PatientDTO =
        PatientRepository.PatientDTO(
            id = PatientId(this[Patients.id].value),
            fio = Fio.fromParts(this[Patients.surname], this[Patients.name], this[Patients.patronymic]),
            dateOfBirth = this[Patients.dateOfBirth],
            gender = PatientGender.from(this[Patients.gender]),
            phone = PhoneNumber(this[Patients.phone]),
            email = this[Patients.email]?.let { PatientEmail(it) },
            addressCountry = CountryCode(this[Patients.addressCountry]),
            addressCity = AddressCity(this[Patients.addressCity]),
            addressStreet = AddressStreet(this[Patients.addressStreet]),
            addressBuilding = AddressBuilding(this[Patients.addressBuilding]),
            addressApartment = this[Patients.addressApartment]?.let { AddressApartment(it) },
            addressPostalCode = this[Patients.addressPostalCode]?.let { AddressPostalCode(it) },
            insuranceNumber = this[Patients.insuranceNumber]?.let { InsuranceNumber(it) },
            insuranceCompany = this[Patients.insuranceCompany]?.let { InsuranceCompany(it) },
            insuranceValidUntil = this[Patients.insuranceValidUntil],
            medicalCardNumber = MedicalCardNumber(this[Patients.medicalCardNumber]),
            registrationDate = this[Patients.registrationDate],
            createdAt = this[Patients.createdAt],
            updatedAt = this[Patients.updatedAt],
            deletedAt = this[Patients.deletedAt]
        )
}
