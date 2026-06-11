package ru.bagdasaryan.springkotlin.medkabinet.storage.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object Patients : IntIdTable("patients") {
    val name = text("first_name")
    val surname = text("last_name")
    val patronymic = text("middle_name").nullable()
    val dateOfBirth = date("date_of_birth")
    val gender = text("gender")
    val phone = text("phone")
    val email = text("email").nullable()

    val addressCountry = text("address_country")
    val addressCity = text("address_city")
    val addressStreet = text("address_street")
    val addressBuilding = text("address_building")
    val addressApartment = text("address_apartment").nullable()
    val addressPostalCode = text("address_postal_code").nullable()

    val insuranceNumber = text("insurance_number").nullable()
    val insuranceCompany = text("insurance_company").nullable()
    val insuranceValidUntil = date("insurance_valid_until").nullable()

    val medicalCardNumber = text("medical_card_number")
    val registrationDate = datetime("registration_date")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    val deletedAt = datetime("deleted_at").nullable()
}
