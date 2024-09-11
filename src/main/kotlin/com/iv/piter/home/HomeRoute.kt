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
import com.vaadin.flow.component.html.Image
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

//        val titleimage = image(Constant.title_image, "..") {
//            width = "25px"
//            height = "25px"
//        }

        verticalLayout(padding = true, spacing = false) {
            setSizeFull()
            content { align(center, top) }

            horizontalLayout(spacing = true, padding = true) {
                setAlignItems(FlexComponent.Alignment.CENTER)
                image(Constant.title_image, "..") {
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

    val trip: Trip get() = row
    private val root = ui {

        verticalLayout(false) {
            text(row.name)
        }
    }
}

