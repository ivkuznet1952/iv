package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.RolesAllowed
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.math.ceil

@Route("shedule", layout = AdminLayout::class)
@PageTitle("Расписание")
@RolesAllowed("Администратор")
class SheduleRoute : KComposite() {

   // private lateinit var header: H5

    private val root = ui {
        verticalLayout(true) {
            content { align(stretch, top) }
            setSizeFull()
            text("SHEDULE")

             br{}
             add(Sheduler(1L))
        }
    }
}

class Sheduler(val guideId:  Long) : VerticalLayout() {

    val today = LocalDate.of(2024, 11, 16)
    val mm: Int = today.month.value
    val yyyy: Int = today.year
    val lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())

    val offbegin = LocalDate.of(yyyy, mm, 1).dayOfWeek.value - 1 // offset first day of month
    val offend = 7 - LocalDate.of(yyyy, mm, lastDayOfMonth.dayOfMonth).dayOfWeek.value // offset last day of month
    //println("offbegin: $offbegin  offend: $offend ")
    //println("offbegin: $offbegin" + " days:" + lastDayOfMonth.dayOfMonth)
    val rows: Int = ceil(((offbegin + lastDayOfMonth.dayOfMonth + offend)/7).toDouble()).toInt()


    init {
        setWidthFull()
        for (i in 1..rows) {
            horizontalLayout(spacing = true, padding = false) {
                setWidthFull()

                for (j in 1..7) {
                    //
                    horizontalLayout(spacing = true, padding = false) {
                        setWidthFull()

                        val s = (i - 1) * 7 + j - offbegin
                        if (s > 0 && s <= lastDayOfMonth.dayOfMonth) {
                            if (j == 6 || j == 7) style.set("border", "red solid 0.001rem")
                            else style.set("border", "gray solid 0.001rem")
                            span {
                                style.set("margin-left", "auto")
                                style.set("margin-right", "2px")
                                text("$s")
                            }
                            style.set("background-color", "gray")
                        }
                        // if (j == 6 || j == 7) style.set("background-color", "red")
                        //else style.set("background-color", "green")
                        //style.set("margin-left", "auto")
                    }

                }
            }
        }
    }
}
