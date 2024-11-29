package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.footer
import com.github.mvysny.karibudsl.v23.header
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.setPrimary
import com.github.vokorm.asc
import com.github.vokorm.exp
import com.iv.piter.Toolbar
import com.iv.piter.entity.Cost
import com.iv.piter.entity.Transport
import com.iv.piter.entity.Trip
import com.iv.piter.entity.setFilterText
import com.iv.piter.toolbarView
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.shared.Tooltip
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.shared.Registration
import eu.vaadinonkotlin.vaadin.setSortProperty
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider
import jakarta.annotation.security.RolesAllowed
import java.io.ByteArrayInputStream
import java.io.InputStream

@Route("trip", layout = AdminLayout::class)
@PageTitle("Экскурсии")
@RolesAllowed("Администратор")
class TripRoute : KComposite() {

    private lateinit var header: H5
    private lateinit var toolbar: Toolbar
    private lateinit var grid: Grid<Trip>
    private lateinit var mgrid: VirtualList<Trip>

    private val editorDialog = TripEditorForm.TripEditorDialog { updateView() }

    private val dataProvider = Trip.dataProvider

    private val root = ui {

        verticalLayout(true) {
            content { align(stretch, top) }
            setSizeFull()
            toolbar = toolbarView("Новая экскурсия") {
                onSearch = { updateView() }
                onCreate = { editorDialog.createNew() }
            }
            header = h5()
            grid = grid(dataProvider) {
                className = "hide-admin-menu"
                isExpand = true
                width = "100%"

                columnFor(Trip::active, createTripActiveCheckboxRenderer()) {
                    isExpand = false
                    setHeader("Активен")
                    width = "10%"
                    isSortable = false
                }

                addColumn(ComponentRenderer<Image, Trip> { tr -> createImage(tr) }).apply {
                    flexGrow = 0; key = "image"
                    isExpand = false
                }

                columnFor(Trip::name) {
                    setHeader("Наименование")
                    setSortProperty(Trip::name.exp)
                }

                addColumn(ComponentRenderer<Button, Trip> { tr -> createEditButton(tr) }).apply {
                    flexGrow = 0
                    key = "edit"
                    isExpand = false
                    width = "4rem"
                }


                addColumn(ComponentRenderer<Button, Trip> { tr -> createCostButton(tr) }).apply {
                    flexGrow = 0
                    key = "cost"
                    isExpand = false
                    width = "4rem"
                }
                element.themeList.add("row-dividers")
            }

            mgrid = virtualList {
                className = "hide-admin-panel"
                setRenderer(ComponentRenderer { row ->
                    val item = TripItem(row)
                    item.onEdit = { edit(row) }
                    item.onCost = {
                        CostModal(row).open(row.name)
                    }
                    item
                })
            }
        }
    }

    init {
        updateView()
    }

    private fun createTripActiveCheckboxRenderer(): ComponentRenderer<Checkbox, Trip> =
        ComponentRenderer { trip ->
            Checkbox(trip.active).apply {
                // when the check box is changed, update the trip and reload the grid
                addValueChangeListener {
                    trip.active = it.value
                    trip.save()
                    grid.dataProvider.refreshAll()
                }
            }
        }

    private fun createImage(trip: Trip): Image {

        if (trip.photo != null) {
            val stream: InputStream = ByteArrayInputStream(trip.photo)
            val imageResource = StreamResource("fileName", InputStreamFactory { stream })
            val image = Image(imageResource, "image")
            image.width = "30px"
            return image
        }
        return Image()
    }

    private fun createEditButton(trip: Trip): Button {
        val editButton = Button("").apply {
            icon = Icon(VaadinIcon.EDIT)
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            height = "22px"
            width = "50px"
            onClick { edit(trip) }
        }

        Tooltip.forComponent(editButton)
            .withText("Редактировать")
            .withPosition(Tooltip.TooltipPosition.TOP_START)
        return editButton
    }

    private fun createCostButton(trip: Trip): Button {
        val costButton = Button("").apply {
            icon = Icon(VaadinIcon.COINS)
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            height = "22px"
            width = "50px"
            onClick {
                CostModal(trip).open(trip.name)
            }
        }
        Tooltip.forComponent(costButton)
            .withText("Стоимость")
            .withPosition(Tooltip.TooltipPosition.TOP_START)
        return costButton
    }

    private fun edit(trip: Trip) {
        editorDialog.edit(trip)
    }

    private fun updateView() {

        dataProvider.setFilterText(toolbar.searchText)
        if (toolbar.searchText.isNotBlank()) {
            header.text = "Поиск “${toolbar.searchText}”"
        } else {
            header.text = "Экскурсия"
        }
        dataProvider.setSortFields(Trip::name.asc)
        grid.dataProvider = dataProvider
        mgrid.dataProvider = dataProvider
    }

}

class TripItem(private val row: Trip) : KComposite() {
    private val trip: Trip get() = row
    var onEdit: () -> Unit = {}
    var onShedule: () -> Unit = {}
    var onCost: () -> Unit = {}

    val binder: Binder<Trip> = beanValidationBinder()

    private val root = ui {
        verticalLayout(spacing = false) {

            style.set("border-top", "gray solid 0.005em")
            style.set("border-radius", "2px")

            horizontalLayout {
                setWidthFull()
                alignItems = FlexComponent.Alignment.CENTER
                checkBox("Активен") {
                    bind(binder).bind(Trip::active)
                    addValueChangeListener {
                        trip.active = it.value
                        trip.save()
                    }
                }

                horizontalLayout {
                    setWidthFull()
                    justifyContentMode = FlexComponent.JustifyContentMode.END

                    val editButton = button {
                        style.set("background-color", "transparent")
                        icon = VaadinIcon.EDIT.create()
                        width = "30px"
                        onClick {
                            onEdit()
                        }
                    }
                    Tooltip.forComponent(editButton)
                        .withText("Редактировать")
                        .withPosition(Tooltip.TooltipPosition.TOP_START)

                    val costButton = button {
                        style.set("background-color", "transparent")
                        icon = VaadinIcon.COINS.create()
                        width = "30px"
                        onClick {
                            onCost()
                        }
                    }
                    Tooltip.forComponent(costButton)
                        .withText("Стоимость")
                        .withPosition(Tooltip.TooltipPosition.TOP_START)

                }
            }
            horizontalLayout {
                setWidthFull()
                alignItems = FlexComponent.Alignment.CENTER
                textField {
                    setWidthFull()
                    bind(binder).bind(Trip::name)
                    isEnabled = false
                }
            }
        }
    }

    init {
        binder.readBean(row)
        binder.validate()
    }

}

internal class CostModal(private val trip: Trip) : Dialog() {

    private lateinit var titleField: H5
    private lateinit var cancelButton: Button
    private var registrationForConfirm: Registration? = null
    private lateinit var costGrid: VirtualList<Cost>
    private val transports = Transport.dataProvider.fetchAll().filter { it.active }

    init {
        addClassNames("confirm-dialog trip-detail-width")
        isCloseOnEsc = true
        isCloseOnOutsideClick = false
        setWidthFull()
        if (transports.isEmpty()) {
            val n = Notification.show(
                "Внимание! Должен быть задан хоть один транспорт!",
                3000,
                Notification.Position.TOP_END
            )
            n.addThemeVariants(NotificationVariant.LUMO_ERROR)
            n.open()
        }
        header {
            horizontalLayout(padding = false, spacing = true) {
                setWidthFull()
                titleField = h5()
                button("Добавить") {
                    icon = VaadinIcon.PLUS.create()
                    style.set("margin-left", "auto !important")
                    setPrimary()
                    onClick { createNew() }
                    isEnabled = transports.isNotEmpty()
                }
            }
        }

        verticalLayout(padding = false, spacing = false) {
            costGrid = virtualList {
                minHeight = "100px"
                height = "auto"
                setRenderer(ComponentRenderer { row ->
                    val item = CostItem(row, transports)
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
                onClick { close() }
            }
        }
        updateView()
    }

    private fun createNew() {
        val cost = Cost()
        cost.trip_id = trip.id
        cost.cost = 0
        if (transports.isNotEmpty()) cost.transport_id = transports[0].id
        cost.save()
        val dp: ListDataProvider<Cost> = ListDataProvider(costGrid.dataProvider.fetchAll())
        dp.items.add(cost)
        costGrid.dataProvider = dp
        updateView()
    }

    private fun updateView() {
        val dp: ListDataProvider<Cost> = ListDataProvider(trip.id?.let { Cost.findByTripId(it) })
        costGrid.dataProvider = dp
    }

    fun open(title: String) {
        titleField.text = title
        registrationForConfirm?.remove()
        open()
    }

}

class CostItem(private val row: Cost, private val transports: List<Transport>) : KComposite() {
    private val cost: Cost get() = row
    var onSave: () -> Unit = {}
    var onDelete: () -> Unit = {}
    val binder: Binder<Cost> = beanValidationBinder()

    private val root = ui {
        horizontalLayout {
            comboBox<Transport>("Транспорт") {
                setSizeFull()
                isAllowCustomValue = false
                setItems(transports)
                value = if (transports.isNotEmpty()) transports.first { it.id == row.transport_id }
                else transports.first()
                setItemLabelGenerator { it.name }
                addValueChangeListener {
                    row.transport_id = value.id
                    onSave()
                }
            }

            integerField("Стоимость") {
                width = "100px"
                bind(binder).bind(Cost::cost)
                addValueChangeListener {
                    if (binder.validate().isOk) onSave()
                }
            }
            val deleteButton = button {
                icon = VaadinIcon.TRASH.create()
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                style.set("color", "white")
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

