package ru.bagdasaryan.springkotlin.medkabinet.domain

@JvmInline
value class UserAccountId(val value: Int) {
    companion object {
        fun create(value: Int): Result<UserAccountId> = runCatching {
            require(value > 0) { "Идентификатор пользователя должен быть положительным" }
            UserAccountId(value)
        }
    }
}
