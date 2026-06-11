package ru.bagdasaryan.springkotlin.medkabinet.storage.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object Doctors : IntIdTable("doctors") {
    val name = text("first_name")
    val surname = text("last_name")
    val patronymic = text("middle_name").nullable()
    val specializationId = integer("specialization_id")
    val departmentId = integer("department_id")
    val licenseNumber = text("license_number")
    val licenseValidUntil = date("license_valid_until")
    val phone = text("phone").nullable()
    val email = text("email").nullable()
    val appointmentDurationMinutes = short("appointment_duration_minutes")
    val isActive = bool("is_active")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
