package ru.bagdasaryan.springkotlin.medkabinet.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class UiExceptionHandler {

    @ExceptionHandler(Throwable::class)
    fun handle(ex: Throwable, request: HttpServletRequest): ResponseEntity<String> {
        val message = ex.message?.takeIf { it.isNotBlank() } ?: "Неизвестная ошибка"

        val status = when (ex) {
            is IllegalArgumentException -> 400
            is NoSuchElementException -> 404
            else -> 500
        }

        val isHtmx = request.getHeader("HX-Request") == "true"

        return if (isHtmx) {
            ResponseEntity.status(status)
                .contentType(MediaType.TEXT_PLAIN)
                .body(message)
        } else {
            ResponseEntity.status(status)
                .contentType(MediaType.TEXT_HTML)
                .body("""<div class="alert alert-danger" role="alert">$message</div>""")
        }
    }
}
