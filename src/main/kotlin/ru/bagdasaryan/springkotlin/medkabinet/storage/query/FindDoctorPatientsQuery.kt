package ru.bagdasaryan.springkotlin.medkabinet.storage.query

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressApartment
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressBuilding
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressCity
import ru.bagdasaryan.springkotlin.medkabinet.domain.AddressStreet
import ru.bagdasaryan.springkotlin.medkabinet.domain.CountryCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.InsuranceCompany
import ru.bagdasaryan.springkotlin.medkabinet.domain.InsuranceNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientEmail
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientGender
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PhoneNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.AddressPostalCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.MedicalCardNumber
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Appointments
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Patients
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class FindDoctorPatientsQuery {

    suspend fun findAllByDoctor(doctorId: DoctorId): Result<List<DoctorPatientDTO>> = runCatching {
        newSuspendedTransaction {
            Appointments
                .join(Patients, JoinType.INNER, Appointments.patientId, Patients.id)
                .selectAll()
                .where { Appointments.doctorId eq doctorId.value }
                .withDistinct()
                .orderBy(Patients.surname to SortOrder.ASC)
                .orderBy(Patients.name to SortOrder.ASC)
                .map { it.toDTO() }
        }
    }

    suspend fun existsRelation(doctorId: DoctorId, patientId: PatientId): Result<Boolean> = runCatching {
        newSuspendedTransaction {
            Appointments
                .selectAll()
                .where { Appointments.doctorId eq doctorId.value }
                .andWhere { Appointments.patientId eq patientId.value }
                .limit(1)
                .any()
        }
    }

    class DoctorPatientDTO(
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

    private fun ResultRow.toDTO(): DoctorPatientDTO =
        DoctorPatientDTO(
            id = PatientId(this[Patients.id].value),
            fio = Fio.fromParts(this[Patients.surname], this[Patients.name], this[Patients.patronymic]),
            dateOfBirth = this[Patients.dateOfBirth],
            gender = PatientGender.from(this[Patients.gender]),
            phone = PhoneNumber(this[Patients.phone]),
            email = this[Patients.email]?.let(::PatientEmail),
            addressCountry = CountryCode(this[Patients.addressCountry]),
            addressCity = AddressCity(this[Patients.addressCity]),
            addressStreet = AddressStreet(this[Patients.addressStreet]),
            addressBuilding = AddressBuilding(this[Patients.addressBuilding]),
            addressApartment = this[Patients.addressApartment]?.let(::AddressApartment),
            addressPostalCode = this[Patients.addressPostalCode]?.let(::AddressPostalCode),
            insuranceNumber = this[Patients.insuranceNumber]?.let(::InsuranceNumber),
            insuranceCompany = this[Patients.insuranceCompany]?.let(::InsuranceCompany),
            insuranceValidUntil = this[Patients.insuranceValidUntil],
            medicalCardNumber = MedicalCardNumber(this[Patients.medicalCardNumber]),
            registrationDate = this[Patients.registrationDate],
            createdAt = this[Patients.createdAt],
            updatedAt = this[Patients.updatedAt],
            deletedAt = this[Patients.deletedAt]
        )
}
