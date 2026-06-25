package ru.bagdasaryan.springkotlin.medkabinet.controller.handler

import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.p
import kotlinx.html.InputType
import kotlinx.html.br
import kotlinx.html.b
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout

@Component
class AuthPageHandler {

    fun renderLoginPage(error: Boolean = false, logout: Boolean = false): String = appLayout(
        pageTitle = "Вход",
        brandHref = "/login",
        nav = emptyList()
    ) {
        div("row justify-content-center") {
            div("col-12 col-md-7 col-lg-5") {
                div("card shadow-sm") {
                    div("card-body p-4") {
                        h1("h4 mb-3") { +"Вход в систему" }
                        p("text-muted mb-4") {
                            +"Используйте учетную запись своей роли: врач, пациент или администратор."
                        }

                        if (error) {
                            div("alert alert-danger") { +"Неверный логин или пароль." }
                        }

                        if (logout) {
                            div("alert alert-success") { +"Вы успешно вышли из системы." }
                        }

                        form(action = "/login", method = FormMethod.post) {
                            div("mb-3") {
                                label {
                                    classes = setOf("form-label")
                                    +"Логин"
                                }
                                input(InputType.text) {
                                    name = "username"
                                    classes = setOf("form-control")
                                    placeholder = "doctor.petrov"
                                    required = true
                                }
                            }
                            div("mb-3") {
                                label {
                                    classes = setOf("form-label")
                                    +"Пароль"
                                }
                                input(InputType.password) {
                                    name = "password"
                                    classes = setOf("form-control")
                                    placeholder = "••••••••"
                                    required = true
                                }
                            }
                            div("d-grid") {
                                button(type = ButtonType.submit) {
                                    classes = setOf("btn", "btn-primary")
                                    +"Войти"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun renderAccessDeniedPage(message: String = "У вас нет доступа к этому разделу"): String = appLayout(
        pageTitle = "Доступ запрещен",
        brandHref = "/",
        nav = emptyList()
    ) {
        div("row justify-content-center") {
            div("col-12 col-md-8 col-lg-6") {
                div("alert alert-danger shadow-sm") {
                    b { +"Доступ запрещен. " }
                    +message
                }
                a(href = "/login") {
                    classes = setOf("btn", "btn-outline-primary")
                    +"На страницу входа"
                }
            }
        }
    }
}
