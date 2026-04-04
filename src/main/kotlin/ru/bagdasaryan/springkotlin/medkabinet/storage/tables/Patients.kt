package ru.bagdasaryan.springkotlin.medkabinet.storage.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object Patients : IntIdTable("patients") {
    val name = text("first_name")
    val surname = text("last_name")
    val patronymic = text("middle_name").nullable()
    val dateOfBirth = date("date_of_birth")
    val gender = text("gender")
    val phone = text("phone")
    val email = text("email").nullable()
    val medicalCardNumber = text("medical_card_number")
    val createdAt = timestampWithTimeZone("created_at")
}
