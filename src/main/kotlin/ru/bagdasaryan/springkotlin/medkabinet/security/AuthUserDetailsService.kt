package ru.bagdasaryan.springkotlin.medkabinet.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.service.UserAccountService

@Service
class AuthUserDetailsService(
    private val userAccountService: UserAccountService
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        userAccountService.findPrincipalByUsername(username).getOrElse { error ->
            throw UsernameNotFoundException(error.message ?: "Пользователь не найден")
        } ?: throw UsernameNotFoundException("Пользователь не найден")
}
