package ru.bagdasaryan.springkotlin.medkabinet.domain.vo

private val PERSON_NAME_REGEX = Regex("^[A-Za-zА-Яа-яЁё\\- ]+$")

@JvmInline
value class Fio private constructor(val value: String) {
    val parts: List<String>
        get() = value.split(" ")

    val surname: String
        get() = parts[0]

    val name: String
        get() = parts[1]

    val patronymic: String?
        get() = parts.getOrNull(2)

    companion object {
        fun of(raw: String): Fio {
            val normalized = raw.trim().replace(Regex("\\s+"), " ")
            val parts = normalized.split(" ")
            require(parts.size in 2..3) { "ФИО должно содержать 2 или 3 части" }
//            require(parts.all { it.length >= 2 }) { "Каждая часть ФИО должна быть не короче 2 символов" }
            require(PERSON_NAME_REGEX.matches(normalized)) { "ФИО содержит недопустимые символы" }
            return Fio(normalized)
        }

        fun fromParts(surname: String, name: String, patronymic: String? = null): Fio =
            of(listOfNotNull(surname, name, patronymic).joinToString(" "))

        fun create(raw: String): Result<Fio> = runCatching { of(raw) }
    }
}
