package ru.bagdasaryan.springkotlin.medkabinet.storage.repository.impls

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.DoctorRepository
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.DoctorRepository.DoctorDTO
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
            val name = fio.name.run { if (this == "") null else this }
            val surname = fio.surname.run { if (this == "") null else this }
            val patronymic = fio.patronymic?.run { if (this == "") null else this }

            Doctors.selectAll().where {
                val base = (surname?.let { Doctors.surname.like("$it%") } ?: Op.TRUE)
                    .and(name?.let { Doctors.name.like("$it%") } ?: Op.TRUE)
                    .and(patronymic?.let { Doctors.patronymic.like("$it%") } ?: Op.TRUE)

                base
            }.map { it.toDTO() }
        }
    }

    fun ResultRow.toDTO() = DoctorDTO(
        firstName = this[Doctors.name],
        lastName = this[Doctors.surname],
        middleName = this[Doctors.patronymic],
        specializationId = this[Doctors.specializationId].toString(),
        departmentId = this[Doctors.departmentId].toString(),
        licenseNumber = this[Doctors.licenseNumber],
        licenseValidUntil = this[Doctors.licenseValidUntil].toString(),
        phone = this[Doctors.phone],
        email = this[Doctors.email]
    )
}