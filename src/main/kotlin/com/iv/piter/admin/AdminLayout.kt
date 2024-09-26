package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.route
import com.github.mvysny.karibudsl.v23.sideNav
import com.github.mvysny.kaributools.navigateTo
import com.iv.piter.MainLayout
import com.iv.piter.security.loginService
import com.vaadin.flow.component.HasElement
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.router.*
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

    private lateinit var contentPane: Div

    private val root = ui {
      appLayout {
            isDrawerOpened = false
           // content { align(stretch, top) }
           // className = "admin-drawer"

            navbar {

                drawerToggle().className = "hide-admin-drawer"
                //width = "960px"
                h6("Панель администратора")
//                    .className = "admin-layout-top"
//                horizontalLayout {
//                    style.set("max-width","360px")
//                    style.set("background-color","green")
//                    text("////EWWWWWW")
//                }
                //classNames.add("app-content")
            }


            drawer {
                //className = "admin-drawer"

                sideNav {
                    //route(WelcomeRoute::class, VaadinIcon.NEWSPAPER)
                  route(UserRoute::class, VaadinIcon.LIST)
                   // route(LoginRoute::class, VaadinIcon.COG)
                }
                    //.className = "admin-drawer"

                    //.route(UserRoute::class, null)
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
                    setSizeFull()
                   // classNames.add("app-content")
                   // style.set("max-width","960px")
                    verticalLayout(spacing = false, padding = true, classNames = "hide-admin-menu") {
//                        content { align(stretch, top) }
                        text("MENU")
                    }
                }
            }

      }

/*            verticalLayout(spacing = false, padding = true) {
                className = "admin-layout"
                content { align(stretch, top) }
                h6("Панель администратора")


                //formLayout(classNames = "view-toolbar-background") {
                horizontalLayout(classNames = "view-toolbar-background") {
                    style.set("padding", "10px")
                    style.set("margin-top", "10px")
                    //style.set("border-bottom", "gray solid 0.005em")
                    // if (ind == 0) style.set("border-top", "gray solid 0.005em")
                    style.set("border-radius", "2px")

//                responsiveSteps {
//                    "0"(1); "320px"(1); "480px"(2)
//                    "780px"(4)
//                }
                    setWidthFull()
                    // style.set("background-color", "#1d3469")

                    routerLink(null, "Пользователи", UserRoute::class) {
                        addClassName("admin-layout__nav-item")
                        highlightCondition = HighlightConditions.sameLocation()
                    }
//                routerLink(null, "Пользователи", UserRoute::class) {
//                     addClassName("main-layout__nav-item")
//                    highlightCondition = HighlightConditions.sameLocation()
//                }
                    button("Выход", VaadinIcon.SIGN_OUT.create()) {
                        addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
                        style.set("margin-left", "auto")
                        onClick {
                            Session.loginService.logout()
                        }
                    }
                }

            } */
        }

//    override fun beforeEnter(event: BeforeEnterEvent?) {
       // s.route(UserRoute::class, null)
//    }
    override fun showRouterLayoutContent(content: HasElement) {
        contentPane.element.appendChild(content.element)
    }
}
