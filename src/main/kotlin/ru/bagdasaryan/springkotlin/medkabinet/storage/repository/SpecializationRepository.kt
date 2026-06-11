package ru.bagdasaryan.springkotlin.medkabinet.storage.repository

import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationId

@Repository
interface SpecializationRepository {
    suspend fun getAll(): Result<List<SpecializationEntity>>
    suspend fun findById(id: SpecializationId): Result<SpecializationEntity?>
}
