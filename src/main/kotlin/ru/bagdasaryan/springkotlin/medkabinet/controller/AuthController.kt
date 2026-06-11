package ru.bagdasaryan.springkotlin.medkabinet.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.bagdasaryan.springkotlin.medkabinet.controller.handler.AuthPageHandler

@RestController
class AuthController(
    private val authPageHandler: AuthPageHandler
) {
    @GetMapping("/login", produces = ["text/html; charset=UTF-8"])
    fun loginPage(
        @RequestParam(name = "error", required = false) error: String?,
        @RequestParam(name = "logout", required = false) logout: String?
    ): ResponseEntity<String> =
        ResponseEntity.ok(
            authPageHandler.renderLoginPage(
                error = error != null,
                logout = logout != null
            )
        )

    @GetMapping("/access-denied", produces = ["text/html; charset=UTF-8"])
    fun accessDeniedPage(): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(authPageHandler.renderAccessDeniedPage())
}
