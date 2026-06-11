package ru.bagdasaryan.springkotlin.medkabinet.controller.handler

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import org.springframework.stereotype.Component
import ru.bagdasaryan.springkotlin.medkabinet.domain.DoctorId
import ru.bagdasaryan.springkotlin.medkabinet.pages.NavItem
import ru.bagdasaryan.springkotlin.medkabinet.pages.appLayout
import ru.bagdasaryan.springkotlin.medkabinet.storage.query.AppointmentLoadQuery
import java.time.LocalDate

@Component
class DashboardPageHandler(
    private val appointmentLoadQuery: AppointmentLoadQuery
) {
    suspend fun renderPage(doctorId: DoctorId? = null): String {
        val today = LocalDate.now()
        val periods = listOf(
            PeriodSpec("Сегодня", today, today),
            PeriodSpec("Вчера", today.minusDays(1), today.minusDays(1)),
            PeriodSpec("Неделя", today.minusDays(6), today),
            PeriodSpec("Месяц", today.minusDays(29), today)
        )

        val stats = periods.map { period ->
            period to appointmentLoadQuery.execute(period.from, period.to, doctorId).getOrThrow()
        }

        return appLayout(
            pageTitle = "Главная",
            brandHref = "/",
            nav = listOf(
                NavItem("Главная", "/", active = true),
                NavItem("Расписание", "/schedule"),
                NavItem("Пациенты", "/patients"),
                NavItem("Врачи", "/doctors"),
                NavItem("Записи", "/appointments")
            )
        ) {
            div("mb-4") {
                h1("h3 mb-1") { +(if (doctorId != null) "Мой дашборд" else "Дашборд нагрузки") }
                p("text-muted mb-0") {
                    +(if (doctorId != null) {
                        "Сводка по вашим записям за разные периоды."
                    } else {
                        "Сводка по записям в разрезе врачей и отделений за разные периоды."
                    })
                }
            }

            div("row g-3 mb-4") {
                stats.forEach { (period, data) ->
                    div("col-12 col-md-6 col-xl-3") {
                        div("card shadow-sm h-100") {
                            div("card-body") {
                                div("text-muted small mb-1") { +period.label }
                                h2("h4 mb-1") { +data.totalAppointments.toString() }
                                div("small text-muted") { +"Записей с ${period.from} по ${period.to}" }
                                div("mt-3 small") {
                                    val topDoctor = data.doctorLoads.firstOrNull()?.doctorFio?.value ?: "-"
                                    val topDepartment = data.departmentLoads.firstOrNull()?.departmentName ?: "-"
                                    div { +"Самый загруженный врач: $topDoctor" }
                                    div { +"Самое загруженное отделение: $topDepartment" }
                                }
                            }
                        }
                    }
                }
            }

            div("row g-4") {
                stats.forEach { (period, data) ->
                    div("col-12") {
                        div("card shadow-sm") {
                            div("card-header d-flex justify-content-between align-items-center flex-wrap gap-2") {
                                div {
                                    strong { +period.label }
                                    span("text-muted ms-2") { +"${period.from} - ${period.to}" }
                                }
                                span("badge rounded-pill bg-primary") { +"Всего: ${data.totalAppointments}" }
                            }
                            div("card-body") {
                                div("row g-3") {
                                    div("col-12 col-lg-6") {
                                        loadTable(
                                            title = "Нагрузка по врачам",
                                            headers = listOf("Врач", "Записей"),
                                            rowsEmptyMessage = "Нет записей за период"
                                        ) {
                                            data.doctorLoads.forEach { row ->
                                                tr {
                                                    td { +row.doctorFio.value }
                                                    td { +row.totalAppointments.toString() }
                                                }
                                            }
                                        }
                                    }
                                    div("col-12 col-lg-6") {
                                        loadTable(
                                            title = "Нагрузка по отделениям",
                                            headers = listOf("Отделение", "Записей"),
                                            rowsEmptyMessage = "Нет записей за период"
                                        ) {
                                            data.departmentLoads.forEach { row ->
                                                tr {
                                                    td { +row.departmentName }
                                                    td { +row.totalAppointments.toString() }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun FlowContent.loadTable(
        title: String,
        headers: List<String>,
        rowsEmptyMessage: String,
        rowsContent: kotlinx.html.TBODY.() -> Unit
    ) {
        div("card h-100 border-0 bg-light") {
            div("card-body") {
                h3("h6 mb-3") { +title }
                div("table-responsive") {
                    table("table table-sm table-striped align-middle mb-0") {
                        thead {
                            tr {
                                headers.forEach { header ->
                                    th { +header }
                                }
                            }
                        }
                        tbody {
                            rowsContent()
                        }
                    }
                }
            }
        }
    }

    private data class PeriodSpec(
        val label: String,
        val from: LocalDate,
        val to: LocalDate
    )
}
