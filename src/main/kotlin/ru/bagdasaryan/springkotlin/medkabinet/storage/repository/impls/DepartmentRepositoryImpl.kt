package ru.bagdasaryan.springkotlin.medkabinet.storage.repository.impls

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentCode
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentEntity
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentFloor
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentId
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentName
import ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentPhone
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.DepartmentRepository
import ru.bagdasaryan.springkotlin.medkabinet.storage.tables.Departments

@Repository
class DepartmentRepositoryImpl : DepartmentRepository {
    override suspend fun getAll(): Result<List<DepartmentEntity>> = runCatching {
        newSuspendedTransaction {
            Departments
                .selectAll()
                .orderBy(Departments.name to SortOrder.ASC)
                .map { it.toEntity() }
        }
    }

    override suspend fun findById(id: DepartmentId): Result<DepartmentEntity?> = runCatching {
        newSuspendedTransaction {
            Departments
                .selectAll()
                .where { Departments.id eq id.value }
                .singleOrNull()
                ?.toEntity()
        }
    }

    private fun ResultRow.toEntity(): DepartmentEntity = DepartmentEntity(
        id = DepartmentId(this[Departments.id].value),
        code = DepartmentCode(this[Departments.code]),
        name = DepartmentName(this[Departments.name]),
        floor = this[Departments.floor]?.let { DepartmentFloor(it) },
        roomNumber = this[Departments.roomNumber]?.let { ru.bagdasaryan.springkotlin.medkabinet.domain.DepartmentRoomNumber(it) },
        phone = this[Departments.phone]?.let { DepartmentPhone(it) },
        isActive = this[Departments.isActive],
        createdAt = this[Departments.createdAt],
        updatedAt = this[Departments.updatedAt]
    )
}
