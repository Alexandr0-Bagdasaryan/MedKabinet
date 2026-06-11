package ru.bagdasaryan.springkotlin.medkabinet.pages

import kotlinx.html.FlowContent
import kotlinx.html.TBODY
import kotlinx.html.ThScope
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.ul

fun <T> FlowContent.renderTableWithPagination(
    cardTitle: String,
    headers: List<String>,
    rows: List<T>,
    page: Int,
    hasPrev: Boolean,
    hasNext: Boolean,
    prevHref: String,
    nextHref: String,
    emptyMessage: String = "Нет данных",
    tbodyId: String? = null,
    rowRenderer: TBODY.(T) -> Unit
) {
    div("card shadow-sm") {
        div("card-header") { +cardTitle }
        div("card-body") {
            div("table-responsive") {
                table("table table-striped table-hover align-middle mb-3") {
                    thead {
                        tr {
                            headers.forEach { header ->
                                th {
                                    scope = ThScope.col
                                    +header
                                }
                            }
                        }
                    }
                    tbody {
                        if (!tbodyId.isNullOrBlank()) {
                            id = tbodyId
                        }

                        if (rows.isEmpty()) {
                            tr {
                                td {
                                    classes = setOf("text-muted", "text-center")
                                    colSpan = headers.size.toString()
                                    +emptyMessage
                                }
                            }
                        } else {
                            rows.forEach { rowRenderer(it) }
                        }
                    }
                }
            }

            ul("pagination mb-0") {
                li(if (hasPrev) "page-item" else "page-item disabled") {
                    a(href = if (hasPrev) prevHref else "#", classes = "page-link") { +"Назад" }
                }
                li("page-item disabled") {
                    span("page-link") { +"Страница $page" }
                }
                li(if (hasNext) "page-item" else "page-item disabled") {
                    a(href = if (hasNext) nextHref else "#", classes = "page-link") { +"Вперед" }
                }
            }
        }
    }
}
