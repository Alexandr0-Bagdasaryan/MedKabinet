package ru.bagdasaryan.springkotlin.medkabinet.vo

data class Fio(
    val surname: String,
    val name: String,
    val patronymic: String? = null
) {
    companion object {
        fun parse(raw: String): Fio {
            val parts = raw.split(" ")
            require(parts.size >= 2) { "Укажите минимум фамилию и имя" }
            return Fio(
                surname = parts[0],
                name = parts[1],
                patronymic = parts.getOrNull(2)
            )
        }
    }
}
