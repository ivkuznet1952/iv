package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.content
import com.github.mvysny.karibudsl.v10.text
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.RolesAllowed

@Route("archive", layout = AdminLayout::class)
@PageTitle("Архив заказов")
@RolesAllowed("Администратор")
class ArchiveRoute : KComposite() {

    //private lateinit var header: H5

    private val root = ui {
        verticalLayout(true) {
            content { align(stretch, top) }
            setSizeFull()
            text("ARCHIVE")
        }
    }
}
