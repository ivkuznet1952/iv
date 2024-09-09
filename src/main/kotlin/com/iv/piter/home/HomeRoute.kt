package com.iv.piter.home

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.iv.piter.MainLayout
import jakarta.annotation.security.PermitAll

@Route("", layout = MainLayout::class)
@PageTitle("Экскурсии")
//@PermitAll
@AnonymousAllowed
class HomeRoute : KComposite() {
    private val root = ui {
        verticalLayout {
            setSizeFull(); isPadding = false
            content { align(center, middle) }
            h1("HOME!")
        }
    }
}

val jvmVersion: String get() = System.getProperty("java.version")
