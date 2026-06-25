package ru.bagdasaryan.springkotlin.medkabinet.storage.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.time

object Schedules : IntIdTable("schedules") {
    val doctorId = integer("doctor_id")
    val workDate = date("work_date")
    val startTime = time("start_time")
    val endTime = time("end_time")
    val isPublished = bool("is_published")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
