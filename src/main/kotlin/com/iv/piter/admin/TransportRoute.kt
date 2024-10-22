package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.ModifierKey
import com.github.mvysny.kaributools.addShortcut
import com.github.mvysny.kaributools.isSingleSelect
import com.github.mvysny.kaributools.sortProperty
import com.github.vokorm.exp
import com.iv.piter.Toolbar
import com.iv.piter.entity.Transport
import com.iv.piter.entity.setFilterText
import com.iv.piter.toolbarView
//import com.iv.tur.Constants
//import com.iv.tur.MainLayout
//import com.iv.tur.Toolbar
//import com.iv.tur.entity.Transport
//import com.iv.tur.entity.setFilterText
//
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.RolesAllowed
//import com.iv.tur.toolbarView
import com.vaadin.flow.component.html.H5
import eu.vaadinonkotlin.vaadin.setSortProperty
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider

@Route("transport", layout = AdminLayout::class)
@PageTitle("Транспорт")
//@RolesAllowed(Constants.ROLES_ROLE_ADMIN)
@RolesAllowed("ROLE_ADMIN")
class TransportRoute : KComposite() {

    private lateinit var header: H5
    private lateinit var toolbar: Toolbar
    private lateinit var grid: Grid<Transport>
    // can't retrieve GridContextMenu from Grid: https://github.com/vaadin/vaadin-grid-flow/issues/523
    lateinit var gridContextMenu: GridContextMenu<Transport>
    private val editorDialog = TransportEditorForm.TransportEditorDialog { updateView() }

    private val dataProvider = Transport.dataProvider

    private val root = ui {

        verticalLayout(true) {
            content { align(stretch, top) }
            toolbar = toolbarView("Новый транспорт") {
                onSearch = { updateView() }
                onCreate = { editorDialog.createNew() }
            }
            header = h5()
            grid = grid(dataProvider) {
                isExpand = true
                columnFor(Transport::name) {
                    //eu.vaadinonkotlin.vaadin.
                    //setSortProperty(Transport::name.exp)
                        setHeader("Имя")
                      //  setSortable(true)
                       // isSortable = false
                        //sortProperty = Transport::name
                        //key = Transport::name.exp.toString()
                       // setKey(Transport::name.toString());
                   //
                }
                addColumn(ComponentRenderer<Button, Transport> { tr -> createEditButton(tr) }).apply {
                    flexGrow = 0; key = ""
                }
                element.themeList.add("row-dividers")

//                gridContextMenu = gridContextMenu {
//                    item("New", { _ -> editorDialog.createNew() })
//                    item("Edit (Alt+E)", { tr -> if (tr != null) edit(tr) })
//                    item("Delete", { tr -> if (tr != null) deleteTransport(tr) })
//                }
            }


            addShortcut(ModifierKey.Alt + Key.KEY_E) {
                val transport = grid.asSingleSelect().value
                if (transport != null) {
                    edit(transport)
                }
            }
        }
    }

    init {
        updateView()
    }

    private fun createEditButton(transport: Transport): Button =
        Button("").apply {
            icon = Icon(VaadinIcon.EDIT)
            addClassName("transport__edit")
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            height = "22px"
            onClick { edit(transport) }
        }

    private fun edit(transport: Transport) {
        editorDialog.edit(transport)
    }

    private fun updateView() {
        //println("//////////// updateView()")
        dataProvider.setFilterText(toolbar.searchText)
        if (toolbar.searchText.isNotBlank()) {
            header.text = "Поиск “${toolbar.searchText}”"
        } else {
            header.text = "Транспорт"
        }
      //  grid.dataProvider = dataProvider
      //  grid.select(grid.dataCommunicator.getItem(0))
    }

    private fun deleteTransport(transport: Transport) {
        transport.delete()
        Notification.show("Транспорт успешно удален...", 3000, Notification.Position.BOTTOM_START)
        updateView()
    }
}
