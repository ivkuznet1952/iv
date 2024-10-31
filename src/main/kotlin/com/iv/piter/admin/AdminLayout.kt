package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.route
import com.github.mvysny.karibudsl.v23.sideNav
import com.iv.piter.MainLayout
import com.iv.piter.security.loginService
import com.vaadin.flow.component.HasElement
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.router.*
import eu.vaadinonkotlin.vaadin.Session
import jakarta.annotation.security.RolesAllowed


/**
 * The Administration view which only administrators may access.
 * The administrator should be able to see/edit the list of users and others data.
 */
@Route("admin", layout = MainLayout::class)
@PageTitle("Администратор")
@RolesAllowed("Администратор")
class AdminLayout : KComposite(), RouterLayout {

    private lateinit var contentPane: Div

    private val root = ui {

        appLayout {

            isDrawerOpened = false

            navbar {
                horizontalLayout(padding = false, spacing = true) {
                    drawerToggle().className = "hide-admin-drawer"
                    style.set("margin-right", "auto")
                    style.set("padding-left", "10px")
                    h6("Панель администратора")
                }
            }


            drawer {
                sideNav {
                    route(CustomerRoute::class, VaadinIcon.ABACUS)
                    route(TransportRoute::class, VaadinIcon.CAR)
                    route(GuideRoute::class, VaadinIcon.COG)
                    route(UserRoute::class, VaadinIcon.LIST)
                   // route(UserRoute::class, VaadinIcon.LIST)
                }

                // logout menu item
                horizontalLayout(padding = true, spacing = false) {
                    button("Logout", VaadinIcon.SIGN_OUT.create()) {
                        addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
                        onClick {
                            Session.loginService.logout()
                        }
                    }
                }

            }

            content {
                contentPane = div("admin-layout") {
                    horizontalLayout (spacing = false, padding = true, classNames = "hide-admin-menu") {
                        routerLink(null, "Заказчики", CustomerRoute::class) {
                            addClassName("admin-layout__nav-item")
                            highlightCondition = HighlightConditions.sameLocation()
                        }
                        routerLink(null, "Транспорт", TransportRoute::class) {
                            addClassName("admin-layout__nav-item")
                            highlightCondition = HighlightConditions.sameLocation()
                        }
                        routerLink(null, "Гиды", GuideRoute::class) {
                            addClassName("admin-layout__nav-item")
                            highlightCondition = HighlightConditions.sameLocation()
                        }
                        routerLink(null,"Пользователи", UserRoute::class) {
                            addClassName("admin-layout__nav-item")
                            highlightCondition = HighlightConditions.sameLocation()
                        }
                        button("Logout", VaadinIcon.SIGN_OUT.create()) {
                            addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
                            style.set("color", "gray")
                            style.set("margin-left", "auto")
                            onClick {
                                Session.loginService.logout()
                            }
                        }
                    }

                }
            }
        }
    }

    override fun showRouterLayoutContent(content: HasElement) {
        contentPane.element.appendChild(content.element)
        root.isDrawerOpened = false
    }
}
