package com.iv.piter.home

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.footer
import com.github.mvysny.karibudsl.v23.header
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.navigateTo
import com.github.mvysny.kaributools.setPrimary
import com.iv.piter.BotConfig
import com.iv.piter.Constant
import com.iv.piter.ExcursTelegramBot
import com.iv.piter.MainLayout
import com.iv.piter.entity.Cost
import com.iv.piter.entity.Trip
import com.iv.piter.security.LoginRoute
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.*
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.shared.Registration
import eu.vaadinonkotlin.vaadin.Session
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider
import org.telegram.telegrambots.meta.TelegramBotsApi
import java.io.ByteArrayInputStream
import java.io.InputStream


//import dev.inmo.tgbotapi.webapps.webApp



//@Route(value = "/23", layout = MainLayout::class)
@Route(value = "", layout = MainLayout::class)
@PageTitle("Экскурсии")
@AnonymousAllowed
//class HomeRoute : KComposite(), BeforeEnterObserver {
    class HomeRoute : KComposite(), HasUrlParameter<String> {


    private lateinit var grid: VirtualList<Trip>
    private lateinit var nodata: H5
    private lateinit var chat: H5

    private var chat_id: String? = null

//    override fun beforeEnter(event: BeforeEnterEvent) {
//        println("######### 000000000000:" + chat_id)
//        chat_id = event.routeParameters["chat_id"].get()
//        println("#########:" + chat_id)
//    }

    // HasUrlParameter interface
 /*   override fun setParameter(event: BeforeEvent?,  @OptionalParameter id: String?) {
        if (id != null) {
            if (event != null) {
                chat_id = event.routeParameters["chat_id"].get()
            }
            println("#########:" + chat_id)
        } else {
            println("######### NOOOOOOO" )
           // navigateTo<HomeRoute>()
        }
    } */

    override fun setParameter(event: BeforeEvent?, @OptionalParameter parameter: String?) {
        if (parameter == null) {
            println("Welcome anonymous.")
        } else {
           // println(String.format("Welcome %s.", parameter))
            chat_id = parameter
            chat.text = parameter.trim()
        }
    }

//    public override fun setParameter(event: BeforeEvent?, parameter: String?) {
       // setText(String.format("Hello, %s!", parameter))
//        println("######### NOOOOOOO:" + parameter)
//    }


    private val root = ui {

        verticalLayout(padding = true, spacing = false) {

            content { align(center, top) }

            horizontalLayout(spacing = true, padding = false,  classNames = "trip-background") {
                setSizeFull()
                alignItems = FlexComponent.Alignment.CENTER
                image(Constant.titleImage, ".") {
                    width = "25px"
                    height = "25px"
                    style.set("margin-left", "5px")
                }
                h6("Индивидуальные экскурсии")
                val room = button("Личный кабинет") {
                    icon = VaadinIcon.COIN_PILES.create()
                    style.set("background-color", "transparent")
                    style.set("color", "whitesmoke")
                    style.set("margin-left", "auto")
                    style.set("font-size", "11px")

                    onClick {
                        if  (BotConfig.bot != null) {
                            BotConfig.bot!!.sendOrder(BotConfig.chatId, "Посылаю техт от $chat_id")
                        }
                        navigateTo<LoginRoute>()
                    }
                }

            }

            text("ID: ")
            chat = h5 {}

            p {  }
            grid = virtualList {
                //style.set("background-color", "orange") // TODO test remove
                setRenderer(ComponentRenderer { row ->
                    setSizeFull()
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

        val costs = row.id?.let { Cost.findByTripId(it) }
        val cost = if (costs == null || costs.isEmpty()) 0 else costs.map{ it.cost }.min()

        horizontalLayout(false, true) {
            setSizeFull()
            height = "90px"
            style.set("padding", "5px")
            style.set("border-bottom", "gray solid 0.005em")
            style.set("border-top", "gray solid 0.005em")
            style.set("border-radius", "2px")

            horizontalLayout(false) {
                alignSelf = FlexComponent.Alignment.CENTER

                if (row.photo != null) {
                    val stream: InputStream = ByteArrayInputStream(row.photo)
                    val imageResource = StreamResource("kot.png", InputStreamFactory { stream })
                    val image = Image(imageResource, "img")
                    image.width = "50px"
                    add(image)
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

                        text("$cost") // TODO
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
    val costs = trip.id?.let { Cost.findByTripId(it) }
    val cost = if (costs == null || costs.isEmpty()) 0 else costs.map{ it.cost }.min()

    init {
        addClassNames("confirm-dialog trip-detail-width")
            isCloseOnEsc = true
        isCloseOnOutsideClick = false

        header {
            titleField = h5()
        }
        horizontalLayout(padding = false, spacing = false) {
            verticalLayout(padding = false,spacing = true) {

                verticalLayout {
                    alignItems = FlexComponent.Alignment.CENTER
                    if (trip.photo != null) {
                        val stream: InputStream = ByteArrayInputStream(trip.photo)
                        val imageResource = StreamResource("kot.png", InputStreamFactory { stream })
                        val image = Image(imageResource, "img")
                        image.width = "250px"
                        add(image)
                    }
                }

                trip.description?.let { text(it) }

                horizontalLayout {
                    span {
                        className = "b-text"
                        text("Стоимость от:")
                    }
                    text("$cost")
                    html("&#x20bd")
                }


                horizontalLayout {
                    span { text("Продолжительность экскурсии:") }
                    text(trip.duration.toString())
                    text(" ч.")
                }

                trip.comment?.let {
                    span {
                        style.set("color", "lightgray")
                        className = "b-font"
                        text(it)
                    }
                }
            }
        }
        footer {
            cancelButton = button("Закрыть") {
                isAutofocus = true
                setPrimary()
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

/*  // SCREEN RESOLUTION NEED TEST
    private fun isMobileView(): Boolean {
        val ui: UI = UI.getCurrent()
        val page: Page = ui.getPage()
        var isScreenWidthLessThan600 = false
        if (page != null) {
            page.retrieveExtendedClientDetails { details ->
                val screenWidth: Int = details.getScreenWidth()
                val screenHeight: Int = details.getScreenHeight()
                isScreenWidthLessThan600 = screenWidth < 600
            }
        }
        return isScreenWidthLessThan600
    }

    fun isMobileDevice(): Boolean {
        val webBrowser = VaadinSession.getCurrent().browser
        return webBrowser.isAndroid || webBrowser.isIPhone || webBrowser.isWindowsPhone
    }

    fun isMobi(): Boolean {
        val userAgent = VaadinService.getCurrentRequest().getHeader("User-Agent")
        val isMobi = userAgent != null && userAgent.lowercase(Locale.getDefault()).contains("mobi")

        return isMobi
    }
*/
