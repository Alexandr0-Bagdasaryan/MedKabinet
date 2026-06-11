package ru.bagdasaryan.springkotlin.medkabinet.storage.repository

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
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface DoctorRepository {
   suspend fun getAll(): Result<List<DoctorDTO>>
   suspend fun findByFio(fio: Fio): Result<List<DoctorDTO>>
   suspend fun findById(id: DoctorId): Result<DoctorDTO?>

    class DoctorDTO(
        val id: DoctorId,
        val fio: Fio,
        val specializationId: SpecializationId,
        val departmentId: DepartmentId,
        val licenseNumber: DoctorLicenseNumber,
        val licenseValidUntil: LocalDate,
        val phone: PhoneNumber?,
        val email: DoctorEmail?,
        val appointmentDurationMinutes: AppointmentDurationMinutes,
        val isActive: DoctorActivityStatus,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
    )
}
