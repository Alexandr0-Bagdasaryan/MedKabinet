package ru.bagdasaryan.springkotlin.medkabinet.storage.repository.impls

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PasswordHash
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.UserAccountId
import ru.bagdasaryan.springkotlin.medkabinet.domain.UserRole
import ru.bagdasaryan.springkotlin.medkabinet.domain.Username
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.UserAccountRepository
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.UserAccounts

@Repository
class UserAccountRepositoryImpl : UserAccountRepository {

    override fun findByUsername(username: Username): Result<UserAccountRepository.UserAccountDTO?> = runCatching {
        transaction {
            UserAccounts
                .selectAll()
                .where { UserAccounts.username eq username.value }
                .limit(1)
                .singleOrNull()
                ?.toDTO()
        }
    }

    private fun org.jetbrains.exposed.sql.ResultRow.toDTO(): UserAccountRepository.UserAccountDTO =
        UserAccountRepository.UserAccountDTO(
            id = UserAccountId(this[UserAccounts.id].value),
            username = Username(this[UserAccounts.username]),
            passwordHash = PasswordHash(this[UserAccounts.passwordHash]),
            role = UserRole.from(this[UserAccounts.role]),
            doctorId = this[UserAccounts.doctorId]?.let { DoctorId(it.value) },
            patientId = this[UserAccounts.patientId]?.let { PatientId(it.value) },
            isActive = this[UserAccounts.isActive],
            createdAt = this[UserAccounts.createdAt],
            updatedAt = this[UserAccounts.updatedAt]
        )
}
