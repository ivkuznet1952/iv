package com.iv.piter

import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.upload.UploadI18N

class UploadRussianI18N : UploadI18N() {
    init {
        dropFiles = DropFiles()
            .setOne("Перетащите файл сюда")
            .setMany("Перетащите файлы сюда")
        addFiles = AddFiles()
            .setOne("Выберите файл...")
            .setMany("Выберите файлы...")
        error = Error()
            .setTooManyFiles("Слишком много файлов.")
            .setFileIsTooBig("Файл слишком большой.")
            .setIncorrectFileType("Неверный формат файла.")
        uploading = Uploading()
            .setStatus(
                Uploading.Status()
                    .setConnecting("Соединение...")
                    .setStalled("Пауза")
                    .setProcessing("Обработка файла...")
                    .setHeld("В одном файле")
            )
            .setRemainingTime(
                Uploading.RemainingTime()
                    .setPrefix("осталось времени: ")
                    .setUnknown("оставшееся время недоступно")
            )
            .setError(
                Uploading.Error()
                    .setServerUnavailable("Сервер не отвечает")
                    .setUnexpectedServerError("Ошибка сервера")
                    .setForbidden("Отклонен")
            )
        units = Units()
            .setSize(listOf("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"))
    }
}

class DatePickerRussianI18N : DatePicker.DatePickerI18n() {

    val years: MutableSet<String> = mutableSetOf(
        "2024", "2025", "2026", "2027", "2028", "2029", "2030"
    )

    init {
        monthNames = listOf(
            "Январь", "Февраль", "Март", "Апрель",
            "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь",
            "Ноябрь", "Декабрь"
        )

        weekdays = listOf(
            "Воскресенье", "Понедельник", "Вторник",
            "Среда", "Черверг", "Пятница", "Суббота"
        )
        weekdaysShort = listOf("Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб")
        today = "Сегодня"
        cancel = "Выйти"
    }

    fun monthNumber(month: String): Int {
        for (i in 0..monthNames.size) {
            if (monthNames[i] == month) return i + 1
        }
        return 0
    }
}

/*
class LineMenuItem(
    val parentid: Long, val id: Long, val name: String, handler: LineMenuHandler, private val totalWidth: String,
    private val toolTipActive: Boolean, selectedFirst: Boolean,
) : KComposite() {

    lateinit var child: Div
    var deleteButton: Button? = null

    var selected: Boolean = selectedFirst
    var active: Boolean = true
    private val pid: Long = id

    private val root = ui {

        verticalLayout(false) {
            div {

                child = div("pitem") {
                    width = totalWidth
                    classNames.set("pitem_not_selected", !selected)
                    classNames.set("pitem_selected", selected)
                    val txt = span {
                        text(name)
                    }
                    if (toolTipActive) {
                        Tooltip.forComponent(txt)
                            .withText(name)
                            .withPosition(Tooltip.TooltipPosition.TOP_START)
                    }
                    onClick {
                        if (!selected) {
                            handler.items.forEach {
                                if (it.selected && (it.pid != pid)) {
                                    it.selected = false
                                    it.child.classNames.set("pitem_selected", false)
                                    it.child.classNames.set("pitem_not_selected", true)
                                    it.deleteButton?.classNames?.set("pclosebutton_selected", false)
                                    it.deleteButton?.classNames?.set("pclosebutton_not_selected", true)
                                }
                            }
                            if (active) selected = !selected
                            if (selected) {
                                classNames.set("pitem_selected", true)
                                deleteButton?.classNames?.set("pclosebutton_selected", true)
                                handler.action(pid)
                            }
                        }
                    }

                    deleteButton = button {
                        icon = VaadinIcon.CLOSE_SMALL.create()
                        classNames.set("pclosebutton_not_selected", !selected)
                        classNames.set("pclosebutton_selected", selected)
                        onClick {
                            if (pid == 0L) {
                                val n = Notification.show("Элемент меню является обязательным и не может быть удален!", 3000, Notification.Position.TOP_END)
                                n.addThemeVariants(NotificationVariant.LUMO_ERROR)
                                n.open()
                            } else {
                                ConfirmationDialog().open(
                                    "Удалить данные?",
                                    "Внимание! Удаленные данные не подлежат восстановлению!",
                                    "",
                                    "Удалить",
                                    isDisruptive = true,
                                    isInform = false,
                                    false
                                ) {
                                    child.isVisible = false;
                                    handler.removeItem(parentid, pid)
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}
*/

/*
class LineMenuHandler(
    private val instance: UpdateLineMenuItem,
    private val itemMap: Map<Long, String?>,
    private val totalWidth: String,
    private val toolTipActive: Boolean,
    val type: Int,
    private val selectedFirst: Boolean,
    private val showDeleteButton: Boolean
) {

    var items: List<LineMenuItem> = createItems()

    init {
        if (selectedFirst && itemMap.isNotEmpty()) {
            itemMap.keys.first()
            action(itemMap.keys.first())
        }
    }

    private fun createItems(): List<LineMenuItem> {
        var list: List<LineMenuItem> = emptyList()
       // println(itemMap)
        var i = 0
        if (!selectedFirst) i = Int.MAX_VALUE
        itemMap.keys.forEach {
            itemMap[it]?.let { it1 ->
                val item = LineMenuItem(0, it, it1, this, totalWidth, toolTipActive, i == 0)
                item.deleteButton?.isEnabled = showDeleteButton
                item.deleteButton?.isVisible = showDeleteButton
                list = list.plus(item)
            }
            i++
        }
        return list
    }

    fun getItems(ind: Int): LineMenuItem = items[ind]

    fun getSelectedId(): Long {
        return items.find { it.selected }?.id ?: return 0
    }

    fun setSelected(id: Long) {
        val item = items.first { it.id == id }
        if (item != null) {
            item.selected = true
            item.child.classNames.set("pitem_selected", true)
            item.deleteButton?.classNames?.set("pclosebutton_selected", true)
            instance.updateItems(type, id)
        }
    }

    fun setSelected(id: Long, value: Boolean) {
        val item = items.find { it.id == id }
        if (item != null) {
            item.selected = false
            if (value) {
                item.child.classNames.set("pitem_not_selected", false)
                item.child.classNames.set("pitem_selected", true)
            } else {
                item.child.classNames.set("pitem_selected", false)
                item.child.classNames.set("pitem_not_selected", true)
            }
        }
    }

    fun removeItem(parentid: Long, id: Long) {
        items = items.filter { it.id != id }
        instance.deleteItem(type, parentid, id)
    }

    fun action(id: Long) {
        instance.updateItems(type, id)
    }

}

interface UpdateLineMenuItem {
    fun updateItems(type: Int, id: Long)

    fun deleteItem(type: Int, parentid: Long, id: Long)
}

infix fun <T> List<T>.prepend(e: T): List<T> {
    return buildList(this.size + 1) {
        add(e)
        addAll(this@prepend)
    }
}

fun <T> List<T>.replaceInList(index: Int, item: T): List<T> = toMutableList().apply { this[index] = item }
*/
