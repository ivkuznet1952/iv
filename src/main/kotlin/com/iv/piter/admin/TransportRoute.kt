package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.vokorm.asc
import com.github.vokorm.exp
import com.iv.piter.Toolbar
import com.iv.piter.entity.Transport
import com.iv.piter.entity.setFilterText
import com.iv.piter.toolbarView
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H5
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


@Route("transport", layout = AdminLayout::class)
@PageTitle("Транспорт")
@RolesAllowed("Администратор")
class TransportRoute : KComposite() {

    private lateinit var header: H5
    private lateinit var toolbar: Toolbar
    private lateinit var grid: Grid<Transport>
    private lateinit var mgrid: VirtualList<Transport>

    private val editorDialog = TransportEditorForm.TransportEditorDialog { updateView() }

    private val dataProvider = Transport.dataProvider

    private val root = ui {

        verticalLayout(true) {
            content { align(stretch, top) }
            setSizeFull()
            toolbar = toolbarView("Новый транспорт") {
                onSearch = { updateView() }
                onCreate = { editorDialog.createNew() }
            }
            header = h5()
            grid = grid(dataProvider) {
                className = "hide-admin-menu"
                isExpand = true
                width = "100%"

                columnFor(Transport::active, createTransportActiveCheckboxRenderer()) {
                    isExpand = false
                    setHeader("Активен")
                    width = "10%"
                    isSortable = false
                }
                columnFor(Transport::name) {
                    setHeader("Наименование")
                    setSortProperty(Transport::name.exp)
                }

                addColumn(ComponentRenderer<Button, Transport> { tr -> createEditButton(tr) }).apply {
                    flexGrow = 0; key = ""
                    isExpand = false
                }

                element.themeList.add("row-dividers")
            }

            mgrid = virtualList {
                className = "hide-admin-panel"
                setRenderer(ComponentRenderer { row ->
                    val item = TransportItem(row)
                    item.onSave = { edit(row) }
                    item
                })
            }

        }
    }

    init {
        updateView()
    }

    private fun createTransportActiveCheckboxRenderer(): ComponentRenderer<Checkbox, Transport> =
        ComponentRenderer { transport ->
            Checkbox(transport.active).apply {
                // when the check box is changed, update the transport and reload the grid
                addValueChangeListener {
                    transport.active = it.value
                    transport.save()
                    grid.dataProvider.refreshAll()
                }
            }
        }


    private fun createEditButton(transport: Transport): Button =
        Button("").apply {
            icon = Icon(VaadinIcon.EDIT)
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            height = "22px"
            onClick { edit(transport) }
        }

    private fun edit(transport: Transport) {
        editorDialog.edit(transport)
    }

    private fun updateView() {

        dataProvider.setFilterText(toolbar.searchText)
        if (toolbar.searchText.isNotBlank()) {
            header.text = "Поиск “${toolbar.searchText}”"
        } else {
            header.text = "Гид"
        }
        dataProvider.setSortFields(Transport::name.asc)
        grid.dataProvider = dataProvider
        mgrid.dataProvider = dataProvider
    }

}

class TransportItem(val row: Transport) : KComposite() {
    val guide: Transport get() = row
    var onSave: () -> Unit = {}
    val binder: Binder<Transport> = beanValidationBinder()

    private val root = ui {
        verticalLayout(spacing = false) {

            style.set("border-top", "gray solid 0.005em")
            style.set("border-radius", "2px")

            horizontalLayout {
                setWidthFull()
                checkBox("Активен") {
                    bind(binder).bind(Transport::active)
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
            textField("Наименование") {
                setWidthFull()
                bind(binder).bind(Transport::name)
                isEnabled = false
            }
          }

    }

    init {
        binder.readBean(row)
        binder.validate()
    }

}
