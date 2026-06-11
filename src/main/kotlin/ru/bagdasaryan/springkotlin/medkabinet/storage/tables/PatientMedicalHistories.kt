package ru.bagdasaryan.springkotlin.medkabinet.storage.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object PatientMedicalHistories : IntIdTable("patient_medical_histories") {
    val patientId = reference("patient_id", Patients)
    val doctorId = reference("doctor_id", Doctors)
    val eventDate = date("event_date")
    val complaint = text("complaint")
    val diagnosis = text("diagnosis")
    val recommendation = text("recommendation")
    val note = text("note").nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
