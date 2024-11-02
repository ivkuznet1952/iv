package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.vokorm.asc
import com.github.vokorm.exp
import com.iv.piter.Toolbar
import com.iv.piter.entity.Trip
import com.iv.piter.entity.setFilterText
import com.iv.piter.toolbarView
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.shared.Tooltip
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin.setSortProperty
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider
import jakarta.annotation.security.RolesAllowed

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
                    flexGrow = 0; key = ""
                    isExpand = false
                }

                element.themeList.add("row-dividers")
            }

            mgrid = virtualList {
                className = "hide-admin-panel"
                setRenderer(ComponentRenderer { row ->
                    val item = TripItem(row)
                    item.onSave = { edit(row) }
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

    private fun createImage(trip: Trip): Image =
        Image().apply {
            src = "images/" + trip.photo
            width = "30px"
        }

    private fun createEditButton(trip: Trip): Button =
        Button("").apply {
            icon = Icon(VaadinIcon.EDIT)
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            height = "22px"
            onClick { edit(trip) }
        }

    private fun edit(trip: Trip) {
        editorDialog.edit(trip)
    }

    private fun updateView() {

        dataProvider.setFilterText(toolbar.searchText)
        if (toolbar.searchText.isNotBlank()) {
            header.text = "Поиск “${toolbar.searchText}”"
        } else {
            header.text = "Гид"
        }
        dataProvider.setSortFields(Trip::name.asc)
        grid.dataProvider = dataProvider
        mgrid.dataProvider = dataProvider
    }

}

class TripItem(val row: Trip) : KComposite() {
    private val guide: Trip get() = row
    var onSave: () -> Unit = {}
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
                        guide.active = it.value
                        guide.save()
                    }
                }

                horizontalLayout {
                    setWidthFull()
                    justifyContentMode = FlexComponent.JustifyContentMode.END

                    val editButton = button {
                        style.set("background-color", "transparent")
                        icon = VaadinIcon.EDIT.create()
                        width = "50px"
                        onClick {
                            onSave()
                        }
                    }
                    Tooltip.forComponent(editButton)
                        .withText("Редактировать")
                        .withPosition(Tooltip.TooltipPosition.TOP_START)
                }
            }
            horizontalLayout {
                setWidthFull()
                alignItems = FlexComponent.Alignment.CENTER
                image {
                    src = "images/" + row.photo
                    width = "30px"
                }
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
