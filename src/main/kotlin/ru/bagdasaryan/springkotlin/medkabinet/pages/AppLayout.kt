package ru.bagdasaryan.springkotlin.medkabinet.pages

import kotlinx.html.*
import kotlinx.html.stream.createHTML

data class NavItem(
    val label: String,
    val href: String,
    val active: Boolean = false
)

private const val BOOTSTRAP_CSS =
    "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
private const val BOOTSTRAP_JS =
    "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
private const val HTMX =
    "https://unpkg.com/htmx.org@1.9.12"

/**
 * Базовый шаблон страницы:
 * - логотип + название
 * - navbar со ссылками
 * - слот под контент (content)
 */
fun appLayout(
    pageTitle: String,
    brandName: String = "MedKabinet",
    brandHref: String = "/",
    logoUrl: String = "/img/logo.svg",   // положи в src/main/resources/static/img/logo.svg
    nav: List<NavItem>,
    content: FlowContent.() -> Unit
): String = createHTML().html {
    head {
        meta { charset = "UTF-8" }
        meta { name = "viewport"; attributes["content"] = "width=device-width, initial-scale=1" }
        title { +pageTitle }
        link(rel = "stylesheet", href = BOOTSTRAP_CSS, type = "text/css")
    }

    body {
        // Navbar (бренд + collapse + ссылки)
        nav {
            classes = setOf("navbar", "navbar-expand-lg", "navbar-dark", "bg-primary")
            div("container") {

                a(href = brandHref, classes = "navbar-brand d-flex align-items-center gap-2") {
                    img(alt = "$brandName logo", src = logoUrl) {
                        width = "28"
                        height = "28"
                        classes = setOf("rounded")
                    }
                    span { +brandName }
                }

                button(classes = "navbar-toggler", type = ButtonType.button) {
                    attributes["data-bs-toggle"] = "collapse"
                    attributes["data-bs-target"] = "#mainNavbar"
                    attributes["aria-controls"] = "mainNavbar"
                    attributes["aria-expanded"] = "false"
                    attributes["aria-label"] = "Toggle navigation"
                    span("navbar-toggler-icon") {}
                }

                div(classes = "collapse navbar-collapse") {
                    id = "mainNavbar"

                    ul("navbar-nav ms-auto mb-2 mb-lg-0") {
                        nav.forEach { item ->
                            li("nav-item") {
                                a(href = item.href, classes = buildString {
                                    append("nav-link")
                                    if (item.active) append(" active")
                                }) {
                                    if (item.active) attributes["aria-current"] = "page"
                                    +item.label
                                }
                            }
                        }
                    }
                }
            }
        } // /navbar

        // Контентная область
        main {
            classes = setOf("container", "py-4")
            content()
        }

        // Footer
        footer {
            classes = setOf("border-top")
            div("container py-3") {
                div { classes = setOf("text-muted", "small"); +"© $brandName" }
            }
        }

        script(src = BOOTSTRAP_JS) {}
        script(src = HTMX) {}
    }
}
