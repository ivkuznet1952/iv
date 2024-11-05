package com.iv.piter.home

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.footer
import com.github.mvysny.karibudsl.v23.header
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.navigateTo
import com.iv.piter.Constant
import com.iv.piter.MainLayout
import com.iv.piter.UploadRussianI18N
import com.iv.piter.entity.Trip
import com.iv.piter.security.LoginRoute
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.shared.Tooltip
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.shared.Registration
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.lang.Thread.sleep
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@Route("", layout = MainLayout::class)
@PageTitle("Экскурсии")
@AnonymousAllowed
class HomeRoute : KComposite() {

    private lateinit var grid: VirtualList<Trip>
    private lateinit var nodata: H5

    @OptIn(ExperimentalEncodingApi::class)
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

//            fun readFileAsTextUsingInputStream(fileName: String): String
//                    = File(fileName).inputStream().readBytes().toString(Charsets.UTF_8)
//            val res = readFileAsTextUsingInputStream("base64.txt")
            //println("////////:" + res.length)
            // val stream1: InputStream = ByteArrayInputStream(res.toByteArray(StandardCharsets.UTF_8))
//            val stream1: InputStream = ByteArrayInputStream(File("base64.txt").inputStream().readBytes())
//            val imageResource1 = StreamResource("base64.txt", InputStreamFactory { stream1 })
//            val image = Image(imageResource1, "base64.txt")
            //image.width = "50px"
//            add(image)
//            image{
//                src="data:image/png;base64,   iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==   "
//            }

//            br{}
//            image{
//                src="data:image/png;base64, " + res
//                width = "150px"
//            }
//            br{}

            // upload image
//            val buffer = MemoryBuffer()
//            val upload = Upload(buffer)
//            upload.isAutoUpload = true

//            val i18n = UploadRussianI18N ()
//            i18n.getAddFiles().setMany("Выберите файл...")
//            upload.i18n = i18n
//            add(upload)
            //val img_path = "src/main/resources/webapp/images/" // TODO
            //println("////////////////user.dir:" + System.getProperties().getProperty("user.dir"))
            //val img_path = "photos/" // TODO
//            upload.addSucceededListener { event ->
//
//                val fileData = buffer.inputStream
//                val fileName = event.getFileName()
//                val bytes = fileData.readBytes()
//                val str = String(bytes, StandardCharsets.UTF_8)
//                val stream: InputStream = ByteArrayInputStream(str.toByteArray(StandardCharsets.UTF_8))
//                val imageResource = StreamResource(fileName, InputStreamFactory { stream })
//                val image = Image(imageResource, "image")
//                image.width = "50px"
//                br {}
//                add(image)
//            }
//                br{}
//                val encodedString:String = Base64.encode(bytes)

            //val imgStr = Base64.decode(encodedString)

//                image{
//                    src="data:image/png;base64, " + encodedString
//                    width = "150px"
//                }
//            }
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

   // @OptIn(ExperimentalEncodingApi::class)
    @OptIn(ExperimentalEncodingApi::class)
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

                if (row.photo != null) {

                    val stream: InputStream = ByteArrayInputStream(row.photo)
                    val imageResource = StreamResource("kot.png", InputStreamFactory { stream })
                    val image = Image(imageResource, "img")
                    image.width = "50px"
                    // image.height = "50px"
                    // br {}
                    add(image)
//                    try {
//                        val inputStream: InputStream = Files.newInputStream(Paths.get("photos/${row.photo}"))
//                        val imageResource = StreamResource(row.photo, InputStreamFactory { inputStream })
//                        val image = Image(imageResource, "image")
//                        image.width = "50px"
//                        add(image)
//                    } catch (e: Exception) {
//                    }
//                    image {
//                        src = "data:image/png;base64, " + row.photo!!
                       // src = "data:image/png;base64, " + Base64.decode(row.photo!!.toByteArray(StandardCharsets.UTF_8))
//                        width = "50px"
//                    }


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

            // upload image
          /*  val buffer = MemoryBuffer()
            val upload = Upload(buffer)
            upload.isAutoUpload = true
            val i18n = UploadRussianI18N()
            i18n.getAddFiles().setMany("Выберите файл...")
            upload.i18n = i18n
            add(upload)
            upload.addSucceededListener { event ->
                val bytes = buffer.inputStream.readBytes()
                row.photo = Base64.encode(bytes)
                //println("////////:" + row.photo!!.length)
                row.save()
            } */
        }

    }

}

@OptIn(ExperimentalEncodingApi::class)
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

                verticalLayout {
                    alignItems = FlexComponent.Alignment.CENTER
                    if (trip.photo != null) {
//                        val inputStream: InputStream = Files.newInputStream(Paths.get("photos/${trip.photo}"))
//                        val imageResource = StreamResource(trip.photo, InputStreamFactory { inputStream })
//                        val image = Image(imageResource, "image")
//                        image.width = "250px"
//                        add(image)
//                        image {
//                            src = "data:image/png;base64, " + trip.photo!!
                             //src = "data:image/png;base64, " + Base64.decode(row.photo!!.toByteArray(StandardCharsets.UTF_8))
//                            width = "250px"
//                        }

                        val stream: InputStream = ByteArrayInputStream(trip.photo)
                        val imageResource = StreamResource("kot.png", InputStreamFactory { stream })
                        val image = Image(imageResource, "img")
                        image.width = "250px"
                       // image.height = "50px"
                       // br {}
                        add(image)
                    }
                }

                trip.description?.let { text(it) }

                horizontalLayout {
                    span {
                        className = "b-text"
                        text("Стоимость от:")
                    }

                    text("1900")
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

