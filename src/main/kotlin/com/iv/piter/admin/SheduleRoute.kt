package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.iv.piter.DatePickerRussianI18N
import com.iv.piter.entity.Guide
import com.iv.piter.entity.Transport
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.orderedlayout.FlexComponent
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

    private lateinit var nodata: H5

    private val guides = Guide.findAll()
    private val datePickerRussianI18N = DatePickerRussianI18N()

    private var guideCombo: ComboBox<Guide> = ComboBox<Guide>()
    private var monthCombo: ComboBox<String> = ComboBox<String>()
    private var yearCombo: ComboBox<String> = ComboBox<String>()
    private var sheduler: Sheduler? = null
    private var vl: VerticalLayout = VerticalLayout()

    private val root = ui {
        verticalLayout(padding = true, spacing = false) {
            content { align(stretch, top) }

            text("Расписание заказов и нерабочего времени гидов")
            if (guides.isNotEmpty()) {
                formLayout {
                    responsiveSteps {
                        "0"(1); "320px"(1); "480px"(1)
                        "580px"(3)
                    }
                    isSpacing = true

                    guideCombo =  comboBox<Guide>("Гид") {
                       // setSizeFull()
                        isAllowCustomValue = false
                        setItems(guides)
                        value = if (guides.isNotEmpty()) guides.first() else null
                        setItemLabelGenerator { it.lastname + " " + it.firstname }
                        addValueChangeListener { updateCalendar() }
                    }

                    monthCombo = comboBox<String>("Месяц") {
                        maxWidth = "200px"
                        setItems(datePickerRussianI18N.monthNames)
                        val today = LocalDate.now()
                        value = datePickerRussianI18N.monthNames[today.month.value - 1]
                        addValueChangeListener { updateCalendar() }
                    }

                    yearCombo = comboBox<String>("Год") {
                        maxWidth = "200px"
                        setItems(datePickerRussianI18N.years)
                        val today = LocalDate.now()
                        value = today.year.toString()
                        addValueChangeListener { updateCalendar() }
                    }
                }
               // br{}
                add(vl)
            }
            verticalLayout (spacing = true) {
                alignItems = FlexComponent.Alignment.CENTER
                span {   nodata = h5 {} }
            }
        }
    }

    private fun updateCalendar() {
        val guide = guideCombo.value
        val mm = datePickerRussianI18N.monthNumber(monthCombo.value)
        val yy: Int = yearCombo.value.toInt()
        val day = LocalDate.of(yy, mm, 1)
        sheduler = guide.id?.let { it1 -> Sheduler(it1, day) }
        vl.removeAll()
        vl.add(sheduler!!)
    }

    init {
       if (guides.isEmpty()) nodata.text = "нет данных"
        else {
           sheduler = guides.first().id?.let { Sheduler(it, LocalDate.now()) }
           vl.removeAll()
           vl.add(sheduler!!)
       }

    }
}

class Sheduler(private val guideId:  Long, day: LocalDate) : VerticalLayout() {

    private val mm: Int = day.month.value
    private val yyyy: Int = day.year
    private val lastDayOfMonth: LocalDate = day.with(TemporalAdjusters.lastDayOfMonth())

    private val offbegin = LocalDate.of(yyyy, mm, 1).dayOfWeek.value - 1 // offset first day of month
    private val offend = 7 - LocalDate.of(yyyy, mm, lastDayOfMonth.dayOfMonth).dayOfWeek.value // offset last days of month
    private val rows: Int = ceil(((offbegin + lastDayOfMonth.dayOfMonth + offend)/7).toDouble()).toInt()

    init {
        setWidthFull()
        for (i in 1..rows) {
            horizontalLayout(spacing = true, padding = false) {
                setWidthFull()

                for (j in 1..7) {
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
                        onClick {
                            println("Clicked $s")
                            style.set("border", "green solid 0.001rem")
                        }
                        // if (j == 6 || j == 7) style.set("background-color", "red")
                        //else style.set("background-color", "green")
                        //style.set("margin-left", "auto")
                    }

                }
            }
        }

        //br{}
        //if (guideId != 0L) text("/////:" + Guide.getById(guideId).lastname + " " + Guide.getById(guideId).firstname)
    }
}
