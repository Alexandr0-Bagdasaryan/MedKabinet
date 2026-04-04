package ru.bagdasaryan.springkotlin.medkabinet.storage.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object Appointments : IntIdTable("appointments") {
    val patientId = integer("patient_id")
    val doctorId = integer("doctor_id")
    val timeSlotId = integer("time_slot_id")
    val appointmentDate = date("appointment_date")
    val startTime = time("start_time")
    val endTime = time("end_time")
    val appointmentType = text("appointment_type")
    val status = text("status")
    val notes = text("notes").nullable()
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
}
