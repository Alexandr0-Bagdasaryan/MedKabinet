package ru.bagdasaryan.springkotlin.medkabinet.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.webFormat(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return format(formatter)
}

fun PhoneNumber.webFormat(): String {
    val digits = value.filter(Char::isDigit)
    if (digits.length == 11 && digits[0] == '7') {
        return "+7 ${digits.substring(1, 4)} ${digits.substring(4, 7)}-${digits.substring(7, 9)}-${digits.substring(9, 11)}"
    }
    return value.trim()
}
