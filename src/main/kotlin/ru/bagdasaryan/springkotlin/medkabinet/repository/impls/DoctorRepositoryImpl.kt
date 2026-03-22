package ru.bagdasaryan.springkotlin.medkabinet.repository.impls

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.exposed.tables.Doctors
import ru.bagdasaryan.springkotlin.medkabinet.repository.DoctorRepository
import ru.bagdasaryan.springkotlin.medkabinet.repository.DoctorRepository.*
import ru.bagdasaryan.springkotlin.medkabinet.vo.Fio

@Repository
class DoctorRepositoryImpl : DoctorRepository {
    override suspend fun getAll(): Result<List<DoctorDTO>> = runCatching {
        newSuspendedTransaction {
            Doctors.selectAll().map { it.toDTO() }
        }
    }

    override suspend fun findByFio(fio: Fio): Result<List<DoctorDTO>> = runCatching {
        newSuspendedTransaction {
            Doctors.selectAll().where {
                val base = Doctors.lastName.lowerCase().like("${fio.lastName.lowercase()}%")
                    .and(Doctors.firstName.lowerCase().like("${fio.firstName.lowercase()}%"))

                fio.middleName?.let {
                    base.and(Doctors.middleName.lowerCase().like("${it.lowercase()}%"))
                } ?: base
            }.map { it.toDTO() }
        }
    }

    fun ResultRow.toDTO() = DoctorDTO(
        firstName = this[Doctors.firstName],
        lastName = this[Doctors.lastName],
        middleName = this[Doctors.middleName],
        specializationId = this[Doctors.specializationId].toString(),
        departmentId = this[Doctors.departmentId].toString(),
        licenseNumber = this[Doctors.licenseNumber],
        licenseValidUntil = this[Doctors.licenseValidUntil].toString(),
        phone = this[Doctors.phone],
        email = this[Doctors.email]
    )
}