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
    brandName: String = "TALUS",
    brandHref: String = "/",
    logoUrl: String = "/img/talus_navbar_logo.svg",
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
            classes = setOf("navbar", "navbar-expand-lg", "navbar-dark", "bg-primary", "py-1")
            div("container") {
                a(href = brandHref, classes = "navbar-brand d-flex align-items-center") {
                    img(alt = "$brandName logo", src = logoUrl) {
                        height = "36"
                        attributes["style"] = "width: auto;"
                    }
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

        div {
            id = "toast-error-container"
            classes = setOf("position-fixed", "bottom-0", "end-0", "p-3")
            attributes["style"] = "z-index: 1080; max-width: 420px;"
        }

        // Footer
        footer {
            classes = setOf("border-top")
            div("container py-3") {
                div { classes = setOf("text-muted", "small"); +"© $brandName" }
            }
        }

        script(src = HTMX) {}
        script(src = BOOTSTRAP_JS) {}

        script {
            unsafe {
                +"""
(function () {
  function showError(message) {
    const container = document.getElementById('toast-error-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = 'toast align-items-center text-bg-danger border-0 show mb-2';
    toast.setAttribute('role', 'alert');
    toast.innerHTML = `
      <div class="d-flex">
        <div class="toast-body">${'$'}{message || 'Произошла ошибка'}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto"></button>
      </div>
    `;

    toast.querySelector('.btn-close')?.addEventListener('click', () => toast.remove());
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 5000);
  }

  document.body.addEventListener('htmx:afterRequest', function (evt) {
    const xhr = evt.detail?.xhr;
    if (!evt.detail?.failed) return;

    const status = xhr?.status ?? 0;
    const reason =
      xhr?.getResponseHeader('X-Error-Message')?.trim() ||
      xhr?.responseText?.trim() ||
      'Неизвестная ошибка';

    showError(`Ошибка ${'$'}{status}: ${'$'}{reason}`);
  });

  document.body.addEventListener('htmx:sendError', function () {
    showError('Ошибка сети: проверьте соединение');
  });
})();
""".trimIndent()
            }
        }
    }
}
