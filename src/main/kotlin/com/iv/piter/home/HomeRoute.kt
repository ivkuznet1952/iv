package com.iv.piter.home

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.footer
import com.github.mvysny.karibudsl.v23.header
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.navigateTo
import com.iv.piter.Constant
import com.iv.piter.MainLayout
import com.iv.piter.entity.Trip
import com.iv.piter.security.LoginRoute
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.shared.Tooltip
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
    private lateinit var nodata: H5

    private val root = ui {

        verticalLayout(padding = true, spacing = false) {

            content { align(center, top) }

            horizontalLayout(spacing = true, padding = false) {
                setSizeFull()
                alignItems = FlexComponent.Alignment.CENTER
                image(Constant.titleImage, ".") {
                    width = "25px"
                    height = "25px"
                }
                h6("Индивидуальные экскурсии")
                val room = button {
                    icon = VaadinIcon.COIN_PILES.create()
                    style.set("background-color", "transparent")
                    style.set("color", "white")
                    style.set("margin-left", "auto")
                    onClick {
                        navigateTo<LoginRoute>()
                    }
                }
                Tooltip.forComponent(room).withText("Личный кабинет")
            }

            grid = virtualList {

                setRenderer(ComponentRenderer { row ->
                    val item = TripListItem(row)
                    item
                }

                )
            }
            nodata = h5 {}
        }

    }

    init {
        val dp: ListDataProvider<Trip> = ListDataProvider(Trip.dataProvider.fetchAll().filter { it.active == true })
        grid.dataProvider = dp
        if (dp.items.size == 0) {
            grid.isVisible = false
            nodata.text = "нет данных"
        }
    }
}


class TripListItem(private val row: Trip) : KComposite() {

    private val root = ui {
        horizontalLayout(false, true, classNames = "main-trip-background") {
            setSizeFull()
            height = "90px"
            style.set("padding", "5px")
            style.set("border-bottom", "gray solid 0.005em")
            style.set("border-top", "gray solid 0.005em")
            style.set("border-radius", "2px")
            horizontalLayout(false) {
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
    private lateinit var cancelButton: Button
    private var registrationForConfirm: Registration? = null

    init {
        addClassNames("confirm-dialog trip-detail-width")
        isCloseOnEsc = true
        isCloseOnOutsideClick = false

        header {
            titleField = h5()
        }
        horizontalLayout(false, false) {
            verticalLayout(false, true) {
                image("images/" + trip.photo, ".") {
                    style.set("margin-top", "24px")
                    setSizeFull()
                }
                horizontalLayout {
                    trip.description?.let { text(it) }
                }
                horizontalLayout {
                    span {
                        className = "b-text"
                        text("Стоимость от:")
                    }

                    text("1900")
                    html("&#x20bd")
                }


                horizontalLayout {
                    span {
                        //
                        text("Продолжительность экскурсии:")
                    }

                    text(trip.duration.toString())
                    text(" ч.")
                }


                horizontalLayout {
                    trip.comment?.let {
                        span {
                            style.set("color", "lightgray")
                            className = "b-font"
                            text(it)
                        }
                    }
                }
            }
        }
        footer {
            cancelButton = button("Закрыть") {
                style.set("background-color", "white")
                style.set("color", "black")
                addThemeVariants(ButtonVariant.LUMO_SMALL)
                onClick { close() }
            }
        }
    }

    fun open(title: String) {
        titleField.text = title
        registrationForConfirm?.remove()
        open()
    }

}


