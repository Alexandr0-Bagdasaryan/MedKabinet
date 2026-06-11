package ru.bagdasaryan.springkotlin.medkabinet.storage.persister

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
interface DoctorPersister {
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
        isActive: DoctorActivityStatus,
        updatedAt: LocalDateTime
    ): Result<Unit>
}
