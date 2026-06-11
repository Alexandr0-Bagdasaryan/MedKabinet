package ru.bagdasaryan.springkotlin.medkabinet.storage.repository

import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentId

@Repository
interface DepartmentRepository {
    suspend fun getAll(): Result<List<DepartmentEntity>>
    suspend fun findById(id: DepartmentId): Result<DepartmentEntity?>
}
