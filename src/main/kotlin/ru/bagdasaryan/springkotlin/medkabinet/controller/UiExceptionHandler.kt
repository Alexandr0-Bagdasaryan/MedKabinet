package ru.bagdasaryan.springkotlin.medkabinet.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.util.HtmlUtils
import java.nio.charset.StandardCharsets


@RestControllerAdvice
class UiExceptionHandler {

    @ExceptionHandler(Throwable::class)
    fun handle(ex: Throwable, request: HttpServletRequest): ResponseEntity<String> {
        val message = ex.message?.takeIf { it.isNotBlank() } ?: "Неизвестная ошибка"
        val safeMessage = HtmlUtils.htmlEscape(message)

        val status = when (ex) {
            is IllegalArgumentException -> 400
            is NoSuchElementException -> 404
            else -> 500
        }

        val isHtmx = request.getHeader("HX-Request") == "true"
        val htmlUtf8 = MediaType.parseMediaType("text/html; charset=UTF-8")
        val textUtf8 = MediaType.parseMediaType("text/plain; charset=UTF-8")

        return if (isHtmx) {
            ResponseEntity.status(status)
                .header("X-Error-Message", message)
                .contentType(textUtf8)
                .body(message)
        } else {
            ResponseEntity.status(status)
                .header("X-Error-Message", message)
                .contentType(htmlUtf8)
                .body(
                    """
                    <!doctype html>
                    <html lang="ru">
                    <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1">
                      <title>Ошибка</title>
                      <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
                    </head>
                    <body class="bg-light">
                      <div class="container py-5">
                        <div class="alert alert-danger shadow-sm" role="alert">
                          $safeMessage
                        </div>
                      </div>
                    </body>
                    </html>
                    """.trimIndent()
                )
        }
    }
}
