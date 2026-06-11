package ru.bagdasaryan.springkotlin.medkabinet.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PasswordHash
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.UserAccountId
import ru.bagdasaryan.springkotlin.medkabinet.domain.UserRole
import ru.bagdasaryan.springkotlin.medkabinet.domain.Username

data class AuthUserPrincipal(
    val id: UserAccountId,
    val usernameValue: Username,
    private val passwordHash: PasswordHash,
    val role: UserRole,
    val doctorId: DoctorId?,
    val patientId: PatientId?,
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf(SimpleGrantedAuthority("ROLE_${role.name}"))

    override fun getPassword(): String = passwordHash.value

    override fun getUsername(): String = usernameValue.value

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
