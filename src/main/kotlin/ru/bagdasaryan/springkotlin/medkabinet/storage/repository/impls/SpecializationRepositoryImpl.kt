package ru.bagdasaryan.springkotlin.medkabinet.storage.repository.impls

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationId
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationName
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.SpecializationRepository
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Specializations

@Repository
class SpecializationRepositoryImpl : SpecializationRepository {
    override suspend fun getAll(): Result<List<SpecializationEntity>> = runCatching {
        newSuspendedTransaction {
            Specializations.selectAll().map { it.toEntity() }
        }
    }

    override suspend fun findById(id: SpecializationId): Result<SpecializationEntity?> = runCatching {
        newSuspendedTransaction {
            Specializations
                .selectAll()
                .where { Specializations.id eq id.value }
                .singleOrNull()
                ?.toEntity()
        }
    }

    private fun ResultRow.toEntity(): SpecializationEntity = SpecializationEntity(
        id = SpecializationId(this[Specializations.id].value),
        code = SpecializationCode(this[Specializations.code]),
        name = SpecializationName(this[Specializations.name]),
        createdAt = this[Specializations.createdAt]
    )
}
