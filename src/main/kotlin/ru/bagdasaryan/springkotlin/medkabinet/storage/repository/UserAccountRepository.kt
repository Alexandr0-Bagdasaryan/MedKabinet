package ru.bagdasaryan.springkotlin.medkabinet.storage.repository

import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PasswordHash
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.UserAccountId
import ru.bagdasaryan.springkotlin.medkabinet.domain.UserRole
import ru.bagdasaryan.springkotlin.medkabinet.domain.Username
import java.time.LocalDateTime

interface UserAccountRepository {

    data class UserAccountDTO(
        val id: UserAccountId,
        val username: Username,
        val passwordHash: PasswordHash,
        val role: UserRole,
        val doctorId: DoctorId?,
        val patientId: PatientId?,
        val isActive: Boolean,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    )

    fun findByUsername(username: Username): Result<UserAccountDTO?>
}
