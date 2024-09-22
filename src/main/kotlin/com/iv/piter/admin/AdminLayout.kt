package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.iv.piter.MainLayout
import com.iv.piter.security.loginService
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.router.HighlightConditions
import com.vaadin.flow.router.RouterLayout
import eu.vaadinonkotlin.vaadin.Session
import jakarta.annotation.security.RolesAllowed

/**
 * The Administration view which only administrators may access.
 * The administrator should be able to see/edit the list of users and others data.
 */
@Route("admin", layout = MainLayout::class)
@PageTitle("Администратор")
@RolesAllowed("ROLE_ADMIN")
class AdminLayout : KComposite(), RouterLayout {

    private val root = ui {
        verticalLayout(spacing = false, padding = true) {
            className = "admin-layout"
            content { align(stretch, top) }
            h6("Панель администратора")
            horizontalLayout {
                routerLink(null, "Пользователи", UserRoute::class) {
                    // addClassName("main-layout__nav-item")
                    highlightCondition = HighlightConditions.sameLocation()
                }
                routerLink(null, "Пользователи", UserRoute::class) {
                    // addClassName("main-layout__nav-item")
                    highlightCondition = HighlightConditions.sameLocation()
                }
                button("Выход", VaadinIcon.SIGN_OUT.create()) {
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
                    style.set("margin-left", "auto")
                    onClick {
                        Session.loginService.logout()
                    }
                }
            }

        }
    }
}
