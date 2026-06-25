package ru.bagdasaryan.springkotlin.medkabinet.storage.persister.impls

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
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
import ru.bagdasaryan.springkotlin.medkabinet.storage.persister.DoctorPersister
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Doctors
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class DoctorPersisterImpl : DoctorPersister {
    override suspend fun updateDoctor(
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
    ): Result<Unit> = runCatching {
        val updated = newSuspendedTransaction {
            Doctors.update({ Doctors.id eq id.value }) {
                it[name] = fio.name
                it[surname] = fio.surname
                it[patronymic] = fio.patronymic
                it[Doctors.specializationId] = specializationId.value
                it[Doctors.departmentId] = departmentId.value
                it[Doctors.licenseNumber] = licenseNumber.value
                it[Doctors.licenseValidUntil] = licenseValidUntil
                it[Doctors.phone] = phone?.value
                it[Doctors.email] = email?.value
                it[Doctors.appointmentDurationMinutes] = appointmentDurationMinutes.value
                it[Doctors.isActive] = isActive == DoctorActivityStatus.ACTIVE
                it[Doctors.updatedAt] = updatedAt
            }
        }
        require(updated > 0) { "Не удалось обновить врача" }
    }
}
