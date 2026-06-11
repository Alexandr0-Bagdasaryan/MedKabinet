package ru.bagdasaryan.springkotlin.medkabinet.storage.repository.impls

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentDurationMinutes
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorEmail
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorLicenseNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.PhoneNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.DoctorActivityStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.DoctorRepository
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors

@Repository
class DoctorRepositoryImpl : DoctorRepository {
    override suspend fun getAll(): Result<List<DoctorRepository.DoctorDTO>> = runCatching {
        newSuspendedTransaction {
            Doctors.selectAll().map { it.toDTO() }
        }
    }

    override suspend fun findByFio(fio: Fio): Result<List<DoctorRepository.DoctorDTO>> = runCatching {
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

    override suspend fun findById(id: DoctorId): Result<DoctorRepository.DoctorDTO?> = runCatching {
        newSuspendedTransaction {
            Doctors.selectAll()
                .where { Doctors.id eq id.value }
                .singleOrNull()
                ?.toDTO()
        }
    }

    private fun ResultRow.toDTO() = DoctorRepository.DoctorDTO(
        id = DoctorId(this[Doctors.id].value),
        fio = Fio.fromParts(this[Doctors.surname], this[Doctors.name], this[Doctors.patronymic]),
        specializationId = SpecializationId(this[Doctors.specializationId]),
        departmentId = DepartmentId(this[Doctors.departmentId]),
        licenseNumber = DoctorLicenseNumber(this[Doctors.licenseNumber]),
        licenseValidUntil = this[Doctors.licenseValidUntil],
        phone = this[Doctors.phone]?.let { PhoneNumber(it) },
        email = this[Doctors.email]?.let { DoctorEmail(it) },
        appointmentDurationMinutes = AppointmentDurationMinutes(this[Doctors.appointmentDurationMinutes]),
        isActive = DoctorActivityStatus.from(this[Doctors.isActive]),
        createdAt = this[Doctors.createdAt],
        updatedAt = this[Doctors.updatedAt]
    )
}
