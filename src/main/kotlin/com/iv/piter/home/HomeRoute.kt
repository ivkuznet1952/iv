package com.iv.piter.home

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.iv.piter.MainLayout
import com.iv.piter.entity.Trip
import com.iv.piter.Constant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
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
    }
}

class TripListItem(private val row: Trip) : KComposite() {

    private val root = ui {

        horizontalLayout(spacing = true, padding = true, classNames = "main-trip-background") {
            setSizeFull()
            alignItems = FlexComponent.Alignment.START
            style.set("border-bottom", "gray solid 0.005em")
            style.set("border-radius", "5px")

                image("images/" + row.photo, ".") {
                    height = "75px"
                }

            verticalLayout(false, false){
                span {
                    text(row.name)
                }
            }

            verticalLayout(false) {
                alignItems = FlexComponent.Alignment.END
                width = "20%"
                button("Заказать") {
                    style.set("background-color", "white")
                    style.set("color", "black")
                }
                span {
                    style.set("font-size","13px")
                    text("1900")
                    html("&#x20bd")
                }

            }
        }
    }
}

