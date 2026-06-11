package ru.bagdasaryan.springkotlin.medkabinet.service

import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.domain.Username
import ru.bagdasaryan.springkotlin.medkabinet.security.AuthUserPrincipal
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.UserAccountRepository

@Service
class UserAccountService(
    private val userAccountRepository: UserAccountRepository
) {
    fun findPrincipalByUsername(username: String): Result<AuthUserPrincipal?> =
        Username.create(username).mapCatching { normalizedUsername ->
            userAccountRepository.findByUsername(normalizedUsername).getOrThrow()?.toPrincipal()
        }

    private fun UserAccountRepository.UserAccountDTO.toPrincipal(): AuthUserPrincipal =
        AuthUserPrincipal(
            id = id,
            usernameValue = username,
            passwordHash = passwordHash,
            role = role,
            doctorId = doctorId,
            patientId = patientId
        )
}
