package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.footer
import com.github.mvysny.karibudsl.v23.header
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.setPrimary
import com.iv.piter.DatePickerRussianI18N
import com.iv.piter.entity.Guide
import com.iv.piter.entity.Shedule
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.shared.Tooltip
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.shared.Registration
import jakarta.annotation.security.RolesAllowed
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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

                    guideCombo = comboBox<Guide>("Гид") {
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
            verticalLayout(spacing = true) {
                alignItems = FlexComponent.Alignment.CENTER
                span { nodata = h5 {} }
            }
        }
    }

    private fun updateCalendar() {
        val guide = guideCombo.value
        val mm = datePickerRussianI18N.monthNumber(monthCombo.value)
        val yy: Int = yearCombo.value.toInt()
        val day = LocalDate.of(yy, mm, 1)
        sheduler = Sheduler(guide, day)
        vl.removeAll()
        vl.add(sheduler!!)
    }

    init {
        if (guides.isEmpty()) nodata.text = "нет данных"
        else {
            sheduler = Sheduler(guides.first(), LocalDate.now())
        }
        vl.removeAll()
        if (sheduler != null) vl.add(sheduler!!)
    }

}


class Sheduler(private val guide: Guide, day: LocalDate) : VerticalLayout() {

    private val mm: Int = day.month.value
    private val yyyy: Int = day.year
    private val lastDayOfMonth: LocalDate = day.with(TemporalAdjusters.lastDayOfMonth())

    private val offbegin = LocalDate.of(yyyy, mm, 1).dayOfWeek.value - 1 // offset first day of month
    private val offend =
        7 - LocalDate.of(yyyy, mm, lastDayOfMonth.dayOfMonth).dayOfWeek.value // offset last days of month
    private val rows: Int = ceil(((offbegin + lastDayOfMonth.dayOfMonth + offend) / 7).toDouble()).toInt()


    init {
        setWidthFull()

        for (i in 1..rows) {
            horizontalLayout(spacing = true, padding = false) {
                setWidthFull()

                for (j in 1..7) {
                   horizontalLayout(spacing = true, padding = false) {
                       //val calendarCell = thi
                           setWidthFull()
                        val s = (i - 1) * 7 + j - offbegin
                        if (s > 0 && s <= lastDayOfMonth.dayOfMonth) {
                            if (j == 6 || j == 7) style.set("border", "red solid 0.001rem")
                            else style.set("border", "gray solid 0.001rem")
                            // TODO //order color
                            var orderExist = false
                            if (s == 29) orderExist = true

                            if (orderExist) {
                                var orderIcon = VaadinIcon.BRIEFCASE.create()
                                orderIcon.className = "order-icon-shedule"
                                add(orderIcon)
                            }
                            span {
                                style.set("margin-left", "auto")
                                style.set("margin-right", "2px")
                                text("$s")
                                if (orderExist) {
                                    style.set("color", "darkgreen")
                                    style.set("font-weight", "900")
                                }
                            }

                             val notWorkTime = guide.id?.let { Shedule.findByGuideId(it, LocalDate.of(yyyy, mm, s)) }.isNullOrEmpty()
                             if (notWorkTime) style.set("background-color", "gray")
                             else style.set("background-color", "red")

                        }
                        onClick {
                            style.set("border", "green solid 0.001rem")

                            val currentDay = LocalDate.of(yyyy, mm, s)
                            val pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            val formattedDate: String = currentDay.format(pattern)
                           // edit
                            SheduleModal(this, guide, currentDay).open(formattedDate)
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

internal class SheduleModal(calendarCell: @VaadinDsl HorizontalLayout, private val guide: Guide, private val day: LocalDate) : Dialog() {

    private lateinit var titleField: H5
    private lateinit var cancelButton: Button
    private var registrationForConfirm: Registration? = null
    private lateinit var sheduleGrid: VirtualList<Shedule>

    init {
        addClassNames("confirm-dialog trip-detail-width")
        isCloseOnEsc = true
        isCloseOnOutsideClick = false
        setWidthFull()
        header {
            horizontalLayout(padding = false, spacing = true) {
                setWidthFull()
                titleField = h5()
            }
        }

        verticalLayout(padding = false, spacing = false) {

            horizontalLayout(spacing = true, padding = false) {
                setWidthFull()
                text("Нерабочее время")
                button("Добавить") {
                    icon = VaadinIcon.PLUS.create()
                    style.set("margin-left", "auto")
                    setPrimary()
                    onClick { createNew() }
                }
            }

            sheduleGrid = virtualList {
                minHeight = "100px"
                height = "auto"
                setRenderer(ComponentRenderer { row ->
                    val item = SheduleItem(row)
                    item.onSave = {
                        item.binder.writeBeanIfValid(row)
                        row.save()
                    }
                    item.onDelete = {
                        if (row.id != null) row.delete()
                        updateView()
                    }
                    item
                })
            }
        }
        footer {
            cancelButton = button("Закрыть") {
                isAutofocus = true
                onClick {
                    close()
                    if  (sheduleGrid.dataCommunicator.itemCount == 0) calendarCell.style.set("background-color", "gray")
                    else calendarCell.style.set("background-color", "red")
                }
            }
        }
        updateView()
    }

    private fun createNew() {
        val shedule = Shedule()
        shedule.guide_id = guide.id
        shedule.day = day
        shedule.start = LocalTime.of(0, 0)
        shedule.finish = LocalTime.of(23, 0)
        shedule.save()
        val dp: ListDataProvider<Shedule> = ListDataProvider(guide.id?.let { Shedule.findByGuideId(it, day) })
        dp.items.add(shedule)
        sheduleGrid.dataProvider = dp
        updateView()
    }

    private fun updateView() {
        val dp: ListDataProvider<Shedule> = ListDataProvider(guide.id?.let { Shedule.findByGuideId(it, day) })
        sheduleGrid.dataProvider = dp
    }

    fun open(title: String) {
        titleField.text = title
        registrationForConfirm?.remove()
        open()
    }

}

class SheduleItem(private val row: Shedule) : KComposite() {
    private val shedule: Shedule get() = row
    var onSave: () -> Unit = {}
    var onDelete: () -> Unit = {}
    val binder: Binder<Shedule> = beanValidationBinder()

    private val root = ui {
        horizontalLayout(spacing = true, padding = false) {
            setWidthFull()

            timePicker("Начало") {
                width = "100px"
                bind(binder).bind(Shedule::start)
                addValueChangeListener {
                    if (binder.validate().isOk) onSave()
                }
            }
            timePicker("Окончание") {
                width = "100px"
                bind(binder).bind(Shedule::finish)
                addValueChangeListener {
                    if (binder.validate().isOk) onSave()
                }
            }

            val deleteButton = button {
                icon = VaadinIcon.TRASH.create()
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                style.set("color", "white")
                style.set("margin-left", "auto")
                onClick { onDelete() }
            }
            Tooltip.forComponent(deleteButton)
                .withText("Удалить")
                .withPosition(Tooltip.TooltipPosition.TOP_START)
        }
    }

    init {
        binder.readBean(row)
        binder.validate()
    }
}
