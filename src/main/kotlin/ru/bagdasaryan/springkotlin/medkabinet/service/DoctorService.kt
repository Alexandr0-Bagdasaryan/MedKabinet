package ru.bagdasaryan.springkotlin.medkabinet.service

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.exposed.tables.Doctors

@Service
class DoctorService {
    fun findAll() = transaction {
        Doctors.selectAll()
            .map { row ->
                DoctorDTO(
                    firstName = row[Doctors.firstName],
                    lastName = row[Doctors.lastName],
                    middleName = row[Doctors.middleName],
                    specializationId = row[Doctors.specializationId].toString(),
                    departmentId = row[Doctors.departmentId].toString(),
                    licenseNumber = row[Doctors.licenseNumber],
                    licenseValidUntil = row[Doctors.licenseValidUntil].toString(),
                    phone = row[Doctors.phone],
                    email = row[Doctors.email]
                )
            }
    }

    class DoctorDTO(
        val firstName: String,
        val lastName: String,
        val middleName: String,
        val specializationId: String,
        val departmentId: String,
        val licenseNumber: String,
        val licenseValidUntil: String,
        val phone: String,
        val email: String
    )
}