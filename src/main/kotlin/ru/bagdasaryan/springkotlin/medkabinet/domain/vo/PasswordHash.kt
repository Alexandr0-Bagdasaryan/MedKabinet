package ru.bagdasaryan.springkotlin.medkabinet.domain

@JvmInline
value class PasswordHash(val value: String) {
    companion object {
        fun create(raw: String): Result<PasswordHash> = runCatching {
            val normalized = raw.trim()
            require(normalized.isNotBlank()) { "Хэш пароля не должен быть пустым" }
            require(normalized.length in 6..255) { "Хэш пароля имеет некорректную длину" }
            PasswordHash(normalized)
        }
    }

    override fun toString(): String = value
}
