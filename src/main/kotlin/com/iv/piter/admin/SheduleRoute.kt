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
import com.vaadin.flow.component.timepicker.TimePicker
import com.vaadin.flow.component.virtuallist.VirtualList
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
                            //if (s == 29) orderExist = true

                            if (orderExist) {
                                val orderIcon = VaadinIcon.BRIEFCASE.create()
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

                            val timePeriods = guide.id?.let { Shedule.findByGuideId(it, LocalDate.of(yyyy, mm, s)) }
                            if (timePeriods != null) {
                                if (timePeriods.isNotEmpty()) {
                                    if (timePeriods.size == 1 && timePeriods[0].start == LocalTime.of(0, 0) &&
                                        timePeriods[0].finish == LocalTime.of(23, 59)
                                    ) {
                                        style.set("background-color", "gray")
                                    } else style.set("background-color", "red")
                                } else style.set("background-color", "gray")
                            } else style.set("background-color", "gray")

                        }
                        onClick {
                            style.set("border", "green solid 0.001rem")
                            val currentDay = LocalDate.of(yyyy, mm, s)
                            val pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            val formattedDate: String = currentDay.format(pattern)
                            // edit
                            SheduleModal(this, guide, currentDay).open(formattedDate)
                        }
                    }

                }
            }
        }
    }

}

internal class SheduleModal(calendarCell: HorizontalLayout, private val guide: Guide, private val day: LocalDate) :
    Dialog() {

    private lateinit var titleField: H5
    private lateinit var errorField: H5
    private lateinit var cancelButton: Button
    private var registrationForConfirm: Registration? = null
    private lateinit var sheduleGrid: VirtualList<Shedule>
    private lateinit var dp: ListDataProvider<Shedule>

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
                text("Рабочее время")
                button("Добавить") {
                    icon = VaadinIcon.EDIT.create()
                    style.set("margin-left", "auto")
                    setPrimary()
                    onClick {
                        createNew()
                    }
                }
            }

            sheduleGrid = virtualList {
                minHeight = "70px"
                height = "auto"

                setRenderer(ComponentRenderer { row ->
                    val item = SheduleItem(row, sheduleGrid.dataCommunicator.itemCount)
                    item.onSave = {
                        if (isErrorTime(row)) {
                            errorField.text = "Ошибка! Данные не сохранены."
                        } else {
                            row.save()
                            updateView()
                        }
                    }
                    item.onDelete = {
                        if (row.id != null) row.delete()
                        updateView()
                        val sheduleList = guide.id?.let { shedules() }

                        if (sheduleList != null && sheduleList.isNotEmpty()) {
                            if (isErrorTime(sheduleList.first())) errorField.text = "Ошибка задания периода времени!"
                        }

                    }
                    item
                })
            }
            errorField = h5() {
                style.set("color", "red")
            }
        }
        footer {
            cancelButton = button("Закрыть") {
                isAutofocus = true
                onClick {
                    close()

                    val sheduleList = shedules()
                    if (sheduleList.isNotEmpty()) {
                        if (sheduleList.size == 1 && sheduleList.first().start == LocalTime.of(0, 0) &&
                            sheduleList.first().finish == LocalTime.of(23, 59)
                        ) {
                            calendarCell.style.set("background-color", "gray")
                        } else calendarCell.style.set("background-color", "red")
                    } else calendarCell.style.set("background-color", "gray")
                }
            }
        }
        updateView()
    }

    private fun isErrorTime(shedule: Shedule): Boolean {

        val sheduleList = shedules()

        if (sheduleList.isEmpty()) return false
        if (sheduleList.size == 1 && shedule.start?.isAfter(shedule.finish) == true) return true
        for (i in sheduleList) {
            if (i.id == shedule.id) continue
            if (shedule.start?.isAfter(shedule.finish) == true) return true
            if (shedule.finish!!.isAfter(i.start) && shedule.start!!.isBefore(i.finish)) return true
            if (i.finish!!.isAfter(shedule.start) && i.start!!.isBefore(shedule.finish)) return true
        }

        return false
    }

    private fun createNew() {
        val shedule = Shedule()
        shedule.guide_id = guide.id
        shedule.day = day
        shedule.start = LocalTime.of(0, 0)
        shedule.finish = LocalTime.of(23, 59)
        shedule.save()
        updateView()
        if (isErrorTime(shedule)) errorField.text = "Ошибка задания периода времени!"
    }

    private fun updateView() {
        errorField.text = ""
        dp = ListDataProvider(guide.id?.let { Shedule.findByGuideId(it, day) })
        sheduleGrid.dataProvider = dp
        val sheduleList = dp.items
        if (sheduleList != null && sheduleList.isEmpty()) {
            createNew()
        }
    }

    fun open(title: String) {
        titleField.text = title
        registrationForConfirm?.remove()
        open()
        val sheduleList = shedules()
        if (sheduleList.isNotEmpty()) {
            if (isErrorTime(sheduleList.first())) errorField.text = "Ошибка задания периода времени!"
        }
    }

    private fun shedules(): MutableCollection<Shedule> = dp.items
}

class SheduleItem(private val row: Shedule, private val count: Int) : KComposite() {
    private val shedule: Shedule get() = row
    var onSave: () -> Unit = {}
    var onDelete: () -> Unit = {}

    private val vdata = HorizontalLayout()
    lateinit var timePanel: EditTimePanel

    private val root = ui {

        horizontalLayout(spacing = true) {
            setWidthFull()
            vdata.setWidthFull()
            add(vdata)
        }
    }

    private fun updateView(state: Boolean = true) {
        vdata.removeAll()
        timePanel = row.finish?.let { row.start?.let { it1 -> EditTimePanel(state, it1, it, count) } }!!
        vdata.add(timePanel)
        timePanel.onClose = {
            updateView(true)
        }
        timePanel.onSave = {
            row.start = timePanel.b.value
            row.finish = timePanel.e.value
            onSave()
            updateView(true)
        }
        timePanel.onEdit = {
            updateView(false)
        }
        timePanel.onDelete = {
            onDelete()
        }
    }


    init {
        updateView(true)
    }
}

class EditTimePanel(val state: Boolean, private var begin: LocalTime, var end: LocalTime, val count: Int) :
    KComposite() {

    var b: TimePicker = TimePicker()
    var e: TimePicker = TimePicker()
    var onClose: () -> Unit = {}
    var onSave: () -> Unit = {}
    var onEdit: () -> Unit = {}
    var onDelete: () -> Unit = {}

    private val root = ui {

        horizontalLayout(spacing = true, padding = false) {
            setWidthFull()
            //style.set("background-color", "red")
            //style.set("background-color", "rgba(61, 61, 88, 0.5)")
            if (!state) {
                b = timePicker("Начало") {
                    value = begin
                    width = "100px"
                    addValueChangeListener {
                        begin = value
                    }
                }
                e = timePicker("Окончание") {
                    value = end
                    width = "100px"
                    addValueChangeListener {
                        end = value
                    }
                }
                val saveButton = button {
                    icon = VaadinIcon.CHECK.create()
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    style.set("margin-left", "auto")
                    onClick { onSave() }
                }

                Tooltip.forComponent(saveButton)
                    .withText("Сохранить")
                    .withPosition(Tooltip.TooltipPosition.TOP_START)

                val closeButton = button {
                    icon = VaadinIcon.CLOSE.create()
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    style.set("color", "white")
                    onClick { onClose() }
                }
                Tooltip.forComponent(closeButton)
                    .withText("Отменить")
                    .withPosition(Tooltip.TooltipPosition.TOP_START)
            } else {

                span {
                   // style.set("color", "darkgreen")

                    style.set("font-weight", "900")
                    text("$begin - $end")
                }

                val editButton = button {
                    icon = VaadinIcon.EDIT.create()
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    style.set("color", "white")
                    style.set("margin-left", "auto")
                    onClick { onEdit() }
                }

                Tooltip.forComponent(editButton)
                    .withText("Редактировать")
                    .withPosition(Tooltip.TooltipPosition.TOP_START)

                val deleteButton = button {
                    icon = VaadinIcon.TRASH.create()
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    style.set("color", "white")
                    isVisible = count > 1
                    onClick { onDelete() }
                }
                Tooltip.forComponent(deleteButton)
                    .withText("Удалить")
                    .withPosition(Tooltip.TooltipPosition.TOP_START)
            }
        }
    }

}