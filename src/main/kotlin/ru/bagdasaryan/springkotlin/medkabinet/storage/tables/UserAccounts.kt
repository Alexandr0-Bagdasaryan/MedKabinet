package ru.bagdasaryan.springkotlin.medkabinet.storage.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object UserAccounts : IntIdTable("user_accounts") {
    val username = text("username").uniqueIndex()
    val passwordHash = text("password_hash")
    val role = text("role")
    val doctorId = reference("doctor_id", Doctors).nullable().uniqueIndex()
    val patientId = reference("patient_id", Patients).nullable().uniqueIndex()
    val isActive = bool("is_active").default(true)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
