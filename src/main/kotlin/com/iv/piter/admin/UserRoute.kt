package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.RolesAllowed

@Route("user", layout = AdminLayout::class)
@PageTitle("Пользователи")
@RolesAllowed("ROLE_ADMIN")
class UserRoute : KComposite() {
    private val root = ui {
        verticalLayout {

            h1("Important content for users")
            text("A page intended for users only. Only users and admins can see this view.")
        }
    }
}
