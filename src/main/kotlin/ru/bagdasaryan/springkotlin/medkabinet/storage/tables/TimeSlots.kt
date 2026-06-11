package ru.bagdasaryan.springkotlin.medkabinet.storage.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.javatime.datetime

object TimeSlots : IntIdTable("time_slots") {
    val scheduleId = integer("schedule_id")
    val doctorId = integer("doctor_id")
    val slotDate = date("slot_date")
    val startTime = time("start_time")
    val endTime = time("end_time")
    val status = text("status")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
