package ru.bagdasaryan.springkotlin.medkabinet.domain

import java.io.Serializable

data class UserAccountId(val value: Int) : Serializable {
    companion object {
        fun create(value: Int): Result<UserAccountId> = runCatching {
            require(value > 0) { "Идентификатор пользователя должен быть положительным" }
            UserAccountId(value)
        }
    }
}
