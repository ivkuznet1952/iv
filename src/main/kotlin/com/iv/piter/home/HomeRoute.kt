package com.iv.piter.home

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.footer
import com.github.mvysny.karibudsl.v23.header
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.fetchAll
import com.iv.piter.Constant
import com.iv.piter.MainLayout
import com.iv.piter.entity.Trip
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer

import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.shared.Registration
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider

@Route("", layout = MainLayout::class)
@PageTitle("Экскурсии")
@AnonymousAllowed
class HomeRoute : KComposite() {

    private lateinit var grid: VirtualList<Trip>
    private var dataProvider = Trip.dataProvider


    private val root = ui {

        verticalLayout(padding = true, spacing = false) {
            setSizeFull()
            content { align(center, top) }

            horizontalLayout(spacing = true, padding = true) {
                setAlignItems(FlexComponent.Alignment.CENTER)
                image(Constant.titleImage, ".") {
                    width = "25px"
                    height = "25px"
                }
                h6("Индивидуальные экскурсии")
            }

            grid = virtualList {
                setRenderer(ComponentRenderer { row ->
                    val item = TripListItem(row)
                    item
                }
                )
            }

        }
    }

    init {
        val dp: ListDataProvider<Trip> = ListDataProvider(Trip.dataProvider.fetchAll())
        grid.dataProvider = dp

        //UI.getCurrent().getPage().retrieveExtendedClientDetails {
           // println("/////////////////////////////:" + it.windowInnerWidth)
        //}
    }
}


class TripListItem(private val row: Trip) : KComposite() {
    private val root = ui {
        horizontalLayout(false, true, classNames = "main-trip-background"){
            setSizeFull()
            height = "100px"
            style.set("padding", "5px")
            style.set("border-bottom", "gray solid 0.005em")
            style.set("border-radius", "2px")
            horizontalLayout {
                alignSelf = FlexComponent.Alignment.CENTER
                image("images/" + row.photo, ".") {
                    width = "50px"
                }
                onClick {
                    ShowDetailModal(row).open(row.name)
                }
            }

            horizontalLayout {
                setSizeFull()
                alignSelf = FlexComponent.Alignment.CENTER

                verticalLayout(false, false) {
                    alignItems = FlexComponent.Alignment.START
                    span {
                        text(row.name)
                    }
                }
                onClick {
                    ShowDetailModal(row).open(row.name)
                }
            }

            horizontalLayout {
                verticalLayout(false) {
                    alignItems = FlexComponent.Alignment.END
                    button("Заказать") {
                        style.set("background-color", "white")
                        style.set("color", "black")
                        addThemeVariants(ButtonVariant.LUMO_SMALL)
                    }
                    span {
                        style.set("font-size", "13px")
                        className = "b-font"
                        text("1900") // TODO
                        html("&#x20bd")
                    }
                }
            }
        }

    }
}

internal class ShowDetailModal(trip: Trip) : Dialog() {

    private lateinit var titleField: H5
//    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button
    private var registrationForConfirm: Registration? = null
//    private val ruDatePicker = DatePickerRussianI18N()

//    private var groupDate: LocalDate? = null
//    private var datePicker: DatePicker

    init {
        addClassNames("confirm-dialog")
        isCloseOnEsc = true
        isCloseOnOutsideClick = false
//        datePicker = DatePicker()
//        datePicker.i18n = ruDatePicker

        header {
            titleField = h5()
        }
        div {
            // labels
            className = "confirm-text"
            text(trip.name)
//            datePicker = datePicker {
//                addValueChangeListener {
//                    groupDate = value
//                }
//            }
        }
        footer {
//            confirmButton = button {
//                addClickListener {
//                    if (datePicker.value != null) close()
//                }
//                isAutofocus = true
//                setPrimary()
//            }
            cancelButton = button("Отмена") {
               // addClickListener { close() }
               // addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                style.set("background-color", "white")
                style.set("color", "black")
                addThemeVariants(ButtonVariant.LUMO_SMALL)
                onClick { close() }
            }
        }
    }

    fun open(title: String) {
        titleField.text = title

//        confirmButton.text = "Сохранить"

        registrationForConfirm?.remove()
//        registrationForConfirm = confirmButton.addClickListener { confirmHandler(groupDate) }
        open()
    }

//    private fun confirmHandler(date: LocalDate?) {
//        if (date == null) {
//            val n = Notification.show("Не задана дата группы!", 3000, Notification.Position.TOP_END)
//            n.addThemeVariants(NotificationVariant.LUMO_ERROR)
//            n.open()
//        } else {
//            val tpackages = TPackage.findAllByTur(editor.tur?.id)
//            if (tpackages.isEmpty()) {
//                val n = Notification.show("Для данного тура не заданы базовые пакеты! Добавьте их в описание тура!", 5000, Notification.Position.TOP_END)
//                n.addThemeVariants(NotificationVariant.LUMO_ERROR)
//                n.open()
//            } else editor.tur?.id?.let { editor.createGroup(it, date) }
//        }
//    }

}


