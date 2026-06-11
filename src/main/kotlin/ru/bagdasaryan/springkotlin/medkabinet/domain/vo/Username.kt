package ru.bagdasaryan.springkotlin.medkabinet.domain

@JvmInline
value class Username(val value: String) {
    companion object {
        fun create(raw: String): Result<Username> = runCatching {
            val normalized = raw.trim()
            require(normalized.isNotBlank()) { "Логин не должен быть пустым" }
            require(normalized.length in 3..64) { "Логин должен содержать от 3 до 64 символов" }
            require(normalized.none(Char::isWhitespace)) { "Логин не должен содержать пробелы" }
            require(normalized.matches(Regex("^[A-Za-z0-9._@-]+$"))) {
                "Логин может содержать только латинские буквы, цифры и символы . _ @ -"
            }
            Username(normalized)
        }
    }

    override fun toString(): String = value
}
