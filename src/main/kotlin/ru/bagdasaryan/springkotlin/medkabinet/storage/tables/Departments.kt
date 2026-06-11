package ru.bagdasaryan.springkotlin.medkabinet.storage.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Departments : IntIdTable("departments") {
    val code = text("code")
    val name = text("name")
    val floor = short("floor").nullable()
    val roomNumber = text("room_number").nullable()
    val phone = text("phone").nullable()
    val isActive = bool("is_active")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
