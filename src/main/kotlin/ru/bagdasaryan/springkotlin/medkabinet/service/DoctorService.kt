package ru.bagdasaryan.springkotlin.medkabinet.service

import org.springframework.stereotype.Service
import ru.bagdasaryan.springkotlin.medkabinet.storage.repository.DoctorRepository
import ru.bagdasaryan.springkotlin.medkabinet.vo.Fio

@Service
class DoctorService(
    private val doctorRepository: DoctorRepository
) {
    suspend fun findAll() = doctorRepository.getAll()

    suspend fun findByFio(fio: Fio) = doctorRepository.findByFio(fio)
}