package ru.bagdasaryan.springkotlin.medkabinet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MedKabinetApplication

fun main(args: Array<String>) {
	runApplication<MedKabinetApplication>(*args)
}
