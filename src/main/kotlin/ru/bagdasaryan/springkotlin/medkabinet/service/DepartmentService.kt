package ru.bagdasaryan.springkotlin.medkabinet.service

import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentId
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.DepartmentRepository

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository
) {
    suspend fun findAll(): Result<List<DepartmentEntity>> = departmentRepository.getAll()
    suspend fun findById(id: DepartmentId): Result<DepartmentEntity?> = departmentRepository.findById(id)
}
