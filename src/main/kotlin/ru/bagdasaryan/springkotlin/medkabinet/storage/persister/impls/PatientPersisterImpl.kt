package ru.bagdasaryan.springkotlin.medkabinet.storage.persister.impls

import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.update
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
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.PatientPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Patients
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class PatientPersisterImpl : PatientPersister {
    override suspend fun createPatient(
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
    ): Result<PatientId> = runCatching {
        val id = newSuspendedTransaction {
            Patients.insertAndGetId {
                it[name] = fio.name
                it[surname] = fio.surname
                it[patronymic] = fio.patronymic
                it[Patients.dateOfBirth] = dateOfBirth
                it[Patients.gender] = gender.value
                it[Patients.phone] = phone.value
                it[Patients.email] = email?.value
                it[Patients.addressCountry] = addressCountry.value
                it[Patients.addressCity] = addressCity.value
                it[Patients.addressStreet] = addressStreet.value
                it[Patients.addressBuilding] = addressBuilding.value
                it[Patients.addressApartment] = addressApartment?.value
                it[Patients.addressPostalCode] = addressPostalCode?.value
                it[Patients.insuranceNumber] = insuranceNumber?.value
                it[Patients.insuranceCompany] = insuranceCompany?.value
                it[Patients.insuranceValidUntil] = insuranceValidUntil
                it[Patients.medicalCardNumber] = medicalCardNumber.value
                it[Patients.registrationDate] = registrationDate
                it[Patients.createdAt] = createdAt
                it[Patients.updatedAt] = updatedAt
                it[Patients.deletedAt] = null
            }
        }
        PatientId(id.value)
    }

    override suspend fun updatePatient(
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
    ): Result<Unit> = runCatching {
        val updated = newSuspendedTransaction {
            Patients.update({ Patients.id eq id.value }) {
                it[name] = fio.name
                it[surname] = fio.surname
                it[patronymic] = fio.patronymic
                it[Patients.dateOfBirth] = dateOfBirth
                it[Patients.gender] = gender.value
                it[Patients.phone] = phone.value
                it[Patients.email] = email?.value
                it[Patients.addressCountry] = addressCountry.value
                it[Patients.addressCity] = addressCity.value
                it[Patients.addressStreet] = addressStreet.value
                it[Patients.addressBuilding] = addressBuilding.value
                it[Patients.addressApartment] = addressApartment?.value
                it[Patients.addressPostalCode] = addressPostalCode?.value
                it[Patients.insuranceNumber] = insuranceNumber?.value
                it[Patients.insuranceCompany] = insuranceCompany?.value
                it[Patients.insuranceValidUntil] = insuranceValidUntil
                it[Patients.medicalCardNumber] = medicalCardNumber.value
                it[Patients.updatedAt] = updatedAt
            }
        }
        require(updated > 0) { "Не удалось обновить пациента" }
    }
}
