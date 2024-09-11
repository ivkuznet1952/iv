package com.iv.piter

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.route
import com.github.mvysny.karibudsl.v23.sideNav
import com.vaadin.flow.component.HasElement
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.router.RouterLayout
import com.iv.piter.admin.AdminRoute
import com.iv.piter.security.loginService
import com.iv.piter.user.UserRoute
//import com.vaadin.securitydemo.welcome.WelcomeRoute
import eu.vaadinonkotlin.vaadin.Session

/**
 * The main layout. It uses the app-layout component which makes the app look like an Android Material app.
 */
class MainLayout : KComposite(), RouterLayout {

    private lateinit var contentPane: Div
    private val root = ui {
        appLayout {
            className="main-layout"

            //navbar {
               // drawerToggle()
               // h3("Vaadin Kotlin Security Demo")
            //}

           /* drawer {
                sideNav {
                    route(WelcomeRoute::class, VaadinIcon.NEWSPAPER)
                    route(UserRoute::class, VaadinIcon.LIST)
                    route(AdminRoute::class, VaadinIcon.COG)
                }
                // logout menu item
                horizontalLayout(padding = true) {
                    button("Logout", VaadinIcon.SIGN_OUT.create()) {
                        addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
                        onClick {
                            Session.loginService.logout()
                        }
                    }
                }
            } */
            content {
                contentPane = div {
                    setSizeFull()
                    classNames.add("app-content")
                }
            }
        }
    }

    override fun showRouterLayoutContent(content: HasElement) {
        contentPane.element.appendChild(content.element)
    }
}
