package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.iv.piter.Toolbar
import com.iv.piter.entity.GOrder
import com.iv.piter.entity.Transport
import com.iv.piter.toolbarView
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider
import jakarta.annotation.security.RolesAllowed

@Route("order", layout = AdminLayout::class)
@PageTitle("Заказы")
@RolesAllowed("Администратор")
class OrderRoute : KComposite() {

    private lateinit var header: H5
    private lateinit var toolbar: Toolbar
    private lateinit var grid: Grid<GOrder>
    private lateinit var mgrid: VirtualList<GOrder>

    private val editorDialog = GOrderEditorForm.GOrderEditorDialog{ updateView() }

    private val dataProvider = GOrder.dataProvider

    private val root = ui {
        verticalLayout(true) {
            content { align(stretch, top) }
            setSizeFull()
            toolbar = toolbarView("Новый заказ") {
                onSearch = { updateView() }
                onCreate = { editorDialog.createNew() }
            }
            header = h5()
        }
    }

    private fun updateView() {

    }
}
