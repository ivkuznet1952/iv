package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.virtualList
import com.iv.piter.Toolbar
import com.iv.piter.entity.*
import com.iv.piter.toolbarView
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.RolesAllowed
import kotlinx.css.Appearance
import java.time.format.DateTimeFormatter

@Route("order", layout = AdminLayout::class)
@PageTitle("Заказы")
@RolesAllowed("Администратор")
class OrderRoute : KComposite() {

    private lateinit var header: H5
    private lateinit var toolbar: Toolbar
    private lateinit var grid: Grid<GOrderDTO>
    private lateinit var mgrid: VirtualList<GOrderDTO>

    private val editorDialog = GOrderEditorForm.GOrderEditorDialog{ updateView() }

    private val dataProvider = GOrderDTO.dataProvider
    private var isActual: Boolean = true
    val pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private val root = ui {
        verticalLayout(true) {
            content { align(stretch, top) }
            setSizeFull()

                toolbar = toolbarView("Новый заказ") {
                    onSearch = { updateView() }
                    onCreate = { editorDialog.createNew() }
                }

            horizontalLayout {
                header = h5()

                checkBox("актуальные") {
                    value = isActual
                    style.set("margin-left","auto")
                    style.set("margin-right","15px")
                    addValueChangeListener {
                        isActual = isActual.not()
                        updateView()
                    }
                }
            }

            grid = grid(dataProvider) {
                className = "hide-admin-menu"
                isExpand = true
                width = "100%"

               //
                addColumn(ComponentRenderer<Text, GOrderDTO> { tr -> createNum(tr) }).apply {
                    isExpand = false
                    setHeader("Номер")
                    width = "10%"
                    isSortable = false
                }



                addColumn(ComponentRenderer<Text, GOrderDTO> { tr -> createDay(tr) }).apply {
                    isExpand = false
                    setHeader("Дата")
                    width = "15%"
                    isSortable = false
                }

                addColumn(ComponentRenderer<Text, GOrderDTO> { tr -> createStart(tr) }).apply {
                    isExpand = false
                    setHeader("Время")
                    width = "10%"
                    isSortable = false
                }

                columnFor(GOrderDTO::trip_name) {
                    setHeader("Экскурсия")
                    width = "40%"
                }
                addColumn(ComponentRenderer<Text, GOrderDTO> { tr -> createPayStatus(tr) }).apply {
                    isExpand = false
                    setHeader("Оплата")
                    width = "15%"
                    isSortable = false
                }

                addColumn(ComponentRenderer<Button, GOrderDTO> { tr -> createEditButton(tr) }).apply {
                    flexGrow = 0; key = ""
                    isExpand = false
                }

                element.themeList.add("row-dividers")
            }


            mgrid = virtualList {
                className = "hide-admin-panel"
                setRenderer(ComponentRenderer { row ->
                    val item = GOrderItem(row)
                    item.onSave = { edit(row) }
                    item
                })
            }


        }
    }

    private fun updateView() {

        dataProvider.setFilterTextGOrderDTO(toolbar.searchText, isActual)
        if (toolbar.searchText.isNotBlank()) {
            header.text = "Поиск “${toolbar.searchText}”"
        } else {
            header.text = "Заказы"
        }
        grid.dataProvider = dataProvider
        mgrid.dataProvider = dataProvider
    }

    init {
        updateView()
    }

    private fun createEditButton(gOrderDTO: GOrderDTO): Button =
        Button("").apply {
            icon = Icon(VaadinIcon.EDIT)
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            height = "22px"
            onClick { edit(gOrderDTO) }
        }

    private fun createNum(gOrderDTO: GOrderDTO): Text =
      Text(gOrderDTO.gOrder?.num.toString())

    private fun createDay(gOrderDTO: GOrderDTO): Text =
        Text(gOrderDTO.gOrder?.day?.format(pattern))

    private fun createStart(gOrderDTO: GOrderDTO): Text =
        Text(gOrderDTO.gOrder?.start.toString())

    private fun createPayStatus(gOrderDTO: GOrderDTO): Text =
        Text(gOrderDTO.gOrder?.paystatus.toString())

    private fun edit(gOrderDTO: GOrderDTO) {
        editorDialog.edit(gOrderDTO)
    }
}

class GOrderItem(val row: GOrderDTO) : KComposite() {
    private val gOrderDTO: GOrderDTO get() = row
    var onSave: () -> Unit = {}
    val binder: Binder<GOrderDTO> = beanValidationBinder()


    private val root = ui {
        verticalLayout(spacing = false) {

            row.trip_name?.let { text(it) }
        }


        }
    init {
        binder.readBean(row)
        binder.validate()
    }
}