package ru.bagdasaryan.springkotlin.medkabinet.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.domain.PatientId
import ru.bagdasaryan.springkotlin.medkabinet.domain.UserRole

@Service
class SecurityScopeService {

    fun currentPrincipal(): AuthUserPrincipal? =
        SecurityContextHolder.getContext().authentication?.principal as? AuthUserPrincipal

    fun currentRole(): UserRole? = currentPrincipal()?.role

    fun currentDoctorId(): DoctorId? = currentPrincipal()?.doctorId

    fun currentPatientId(): PatientId? = currentPrincipal()?.patientId

    fun isAdmin(): Boolean = currentRole() == UserRole.ADMIN

    fun isDoctor(): Boolean = currentRole() == UserRole.DOCTOR

    fun isPatient(): Boolean = currentRole() == UserRole.PATIENT
}
