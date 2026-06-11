package ru.bagdasaryan.springkotlin.medkabinet.domain.entity

import ru.bagdasaryan.springkotlin.medkabinet.domain.AppointmentDurationMinutes
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorEmail
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorLicenseNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.PhoneNumber
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationId
import ru.bagdasaryan.springkotlin.medkabinet.domain.vo.Fio
import ru.bagdasaryan.springkotlin.medkabinet.service.AppointmentService
import java.time.LocalDate
import java.time.LocalDateTime

 class DoctorEntity(
    val id: DoctorId,
    val fio: Fio,
    val specializationId: SpecializationId,
    val departmentId: DepartmentId,
    val licenseNumber: DoctorLicenseNumber,
    val licenseValidUntil: LocalDate,
    val phone: PhoneNumber?,
    val email: DoctorEmail?,
    val appointmentDurationMinutes: AppointmentDurationMinutes,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)


