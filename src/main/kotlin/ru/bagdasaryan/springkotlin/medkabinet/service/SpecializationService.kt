package ru.bagdasaryan.springkotlin.medkabinet.service

import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.SpecializationId
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.SpecializationRepository

@Service
class SpecializationService(
    private val specializationRepository: SpecializationRepository
) {
    suspend fun findAll(): Result<List<SpecializationEntity>> = specializationRepository.getAll()
    suspend fun findById(id: SpecializationId): Result<SpecializationEntity?> = specializationRepository.findById(id)
}
