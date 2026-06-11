package ru.bagdasaryan.springkotlin.medkabinet.service

import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentDurationMinutes
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorEmail
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorLicenseNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.PhoneNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationId
import ru.bagdasaryan.springkotlin.medkabinet.domain.entity.DoctorEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.DoctorActivityStatus
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.DoctorPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.DoctorRepository
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class DoctorService(
    private val doctorRepository: DoctorRepository,
    private val doctorPersister: DoctorPersister
) {
    suspend fun findAll(): Result<List<DoctorEntity>> =
        doctorRepository.getAll().mapCatching { rows -> rows.map { it.toEntity() } }

    suspend fun findByFio(fio: Fio): Result<List<DoctorEntity>> =
        doctorRepository.findByFio(fio).mapCatching { rows -> rows.map { it.toEntity() } }

    suspend fun findById(id: DoctorId): Result<DoctorEntity?> =
        doctorRepository.findById(id).mapCatching { it?.toEntity() }

    suspend fun updateDoctor(
        id: DoctorId,
        fio: Fio,
        specializationId: SpecializationId,
        departmentId: DepartmentId,
        licenseNumber: DoctorLicenseNumber,
        licenseValidUntil: LocalDate,
        phone: PhoneNumber?,
        email: DoctorEmail?,
        appointmentDurationMinutes: AppointmentDurationMinutes,
        isActive: DoctorActivityStatus
    ): Result<Unit> = runCatching {
        doctorPersister.updateDoctor(
            id = id,
            fio = fio,
            specializationId = specializationId,
            departmentId = departmentId,
            licenseNumber = licenseNumber,
            licenseValidUntil = licenseValidUntil,
            phone = phone,
            email = email,
            appointmentDurationMinutes = appointmentDurationMinutes,
            isActive = isActive,
            updatedAt = LocalDateTime.now()
        ).getOrThrow()
    }

    private fun DoctorRepository.DoctorDTO.toEntity(): DoctorEntity =
        DoctorEntity(
            id = id,
            fio = fio,
            specializationId = specializationId,
            departmentId = departmentId,
            licenseNumber = licenseNumber,
            licenseValidUntil = licenseValidUntil,
            phone = phone,
            email = email,
            appointmentDurationMinutes = appointmentDurationMinutes,
            isActive = isActive == DoctorActivityStatus.ACTIVE,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}
