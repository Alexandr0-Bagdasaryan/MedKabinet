package ru.bagdasaryan.springkotlin.medkabinet.domain
@JvmInline
value class CancellationReason(val value: String) {
    init { require(value.isNotBlank()) { "Причина отмены не должна быть пустой" } }
}
