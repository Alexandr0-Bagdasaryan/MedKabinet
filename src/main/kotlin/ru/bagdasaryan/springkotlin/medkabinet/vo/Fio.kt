package ru.bagdasaryan.springkotlin.medkabinet.vo

data class Fio(
    val lastName: String,
    val firstName: String,
    val middleName: String? = null
) {
    companion object {
        fun parse(raw: String): Fio {
            val parts = raw.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
            require(parts.size >= 2) { "Укажите минимум фамилию и имя" }
            return Fio(
                lastName = parts[0],
                firstName = parts[1],
                middleName = parts.getOrNull(2)
            )
        }
    }
}
