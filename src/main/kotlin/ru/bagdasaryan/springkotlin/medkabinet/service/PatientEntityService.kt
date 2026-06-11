package ru.bagdasaryan.springkotlin.medkabinet.service

import org.springframework.stereotype.Service
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
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientGender
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PhoneNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.PatientRepository
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.PatientPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.FindDoctorPatientsQuery
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class PatientService(
    private val patientRepository: PatientRepository,
    private val patientPersister: PatientPersister,
    private val findDoctorPatientsQuery: FindDoctorPatientsQuery
) {
    suspend fun findAll(): Result<List<PatientEntity>> =
        patientRepository.getAll().mapCatching { rows -> rows.map { it.toEntity() } }

    suspend fun findAllByDoctor(doctorId: DoctorId): Result<List<PatientEntity>> =
        findDoctorPatientsQuery.findAllByDoctor(doctorId).mapCatching { rows ->
            rows.map { it.toEntity() }.distinctBy { it.id.value }
        }

    suspend fun findById(id: PatientId): Result<PatientEntity?> =
        patientRepository.findById(id).mapCatching { it?.toEntity() }

    suspend fun existsById(id: PatientId): Result<Boolean> =
        patientRepository.existsById(id)

    suspend fun isAssignedToDoctor(doctorId: DoctorId, patientId: PatientId): Result<Boolean> =
        findDoctorPatientsQuery.existsRelation(doctorId, patientId)

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
        medicalCardNumber: MedicalCardNumber
    ): Result<PatientId> = runCatching {
        val now = LocalDateTime.now()
        patientPersister.createPatient(
            fio = fio,
            dateOfBirth = dateOfBirth,
            gender = gender,
            phone = phone,
            email = email,
            addressCountry = addressCountry,
            addressCity = addressCity,
            addressStreet = addressStreet,
            addressBuilding = addressBuilding,
            addressApartment = addressApartment,
            addressPostalCode = addressPostalCode,
            insuranceNumber = insuranceNumber,
            insuranceCompany = insuranceCompany,
            insuranceValidUntil = insuranceValidUntil,
            medicalCardNumber = medicalCardNumber,
            registrationDate = now,
            createdAt = now,
            updatedAt = now
        ).getOrThrow()
    }

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
        medicalCardNumber: MedicalCardNumber
    ): Result<Unit> = runCatching {
        val now = LocalDateTime.now()
        patientPersister.updatePatient(
            id = id,
            fio = fio,
            dateOfBirth = dateOfBirth,
            gender = gender,
            phone = phone,
            email = email,
            addressCountry = addressCountry,
            addressCity = addressCity,
            addressStreet = addressStreet,
            addressBuilding = addressBuilding,
            addressApartment = addressApartment,
            addressPostalCode = addressPostalCode,
            insuranceNumber = insuranceNumber,
            insuranceCompany = insuranceCompany,
            insuranceValidUntil = insuranceValidUntil,
            medicalCardNumber = medicalCardNumber,
            updatedAt = now
        ).getOrThrow()
    }

    private fun PatientRepository.PatientDTO.toEntity(): PatientEntity =
        PatientEntity(
            id = id,
            fio = fio,
            dateOfBirth = dateOfBirth,
            gender = gender,
            phone = phone,
            email = email,
            addressCountry = addressCountry,
            addressCity = addressCity,
            addressStreet = addressStreet,
            addressBuilding = addressBuilding,
            addressApartment = addressApartment,
            addressPostalCode = addressPostalCode,
            insuranceNumber = insuranceNumber,
            insuranceCompany = insuranceCompany,
            insuranceValidUntil = insuranceValidUntil,
            medicalCardNumber = medicalCardNumber,
            registrationDate = registrationDate,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )

    private fun FindDoctorPatientsQuery.DoctorPatientDTO.toEntity(): PatientEntity =
        PatientEntity(
            id = id,
            fio = fio,
            dateOfBirth = dateOfBirth,
            gender = gender,
            phone = phone,
            email = email,
            addressCountry = addressCountry,
            addressCity = addressCity,
            addressStreet = addressStreet,
            addressBuilding = addressBuilding,
            addressApartment = addressApartment,
            addressPostalCode = addressPostalCode,
            insuranceNumber = insuranceNumber,
            insuranceCompany = insuranceCompany,
            insuranceValidUntil = insuranceValidUntil,
            medicalCardNumber = medicalCardNumber,
            registrationDate = registrationDate,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
}
