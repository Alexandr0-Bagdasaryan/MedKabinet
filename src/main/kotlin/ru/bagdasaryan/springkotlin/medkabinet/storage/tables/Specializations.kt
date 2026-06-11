package ru.bagdasaryan.springkotlin.medkabinet.storage.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Specializations : IntIdTable("specializations") {
    val code = text("code")
    val name = text("name")
    val createdAt = datetime("created_at")
}