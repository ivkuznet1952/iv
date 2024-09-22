package com.iv.piter

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasElement
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.router.RouterLayout
//import com.vaadin.securitydemo.welcome.WelcomeRoute

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
