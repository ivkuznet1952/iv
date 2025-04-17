package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.github.vokorm.asc
import com.github.vokorm.exp
import com.iv.piter.security.User
import com.iv.piter.security.setFilterText
import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin.setSortProperty
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider
import jakarta.annotation.security.RolesAllowed


@Route("archive", layout = AdminLayout::class)
@PageTitle("Архив заказов")
@RolesAllowed("Администратор")
class ArchiveRoute : KComposite() {

    //private lateinit var grid: Grid<User>
    private val dataProvider = User.dataProvider
    var panel = VerticalLayout().apply {}
//var showTur = false

    private val root = ui {
        flexLayout  {
            //content { align(stretch, top) }

            verticalLayout {
                width = "25%"
                //minHeight = "30%"
                style.set("background-color", "white")
                button("SHOW") {
                   // icon = Icon(VaadinIcon.EDIT)
                   // addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    //height = "22px"
                    setPrimary()
                    onClick { addTable() }
                }
                add(TurView("name"))
            }
             panel = verticalLayout(spacing = true, padding = false) {
                width = "75%"
                //minHeight = "30%"
                //style.set("background-color", "blue")

                    style.set("padding", "40px !important")
                    style.set("background-color", "white")
                    style.set("border-radius", "8px")
//                    width = "50%"
               // text("ARCHIVE")

                val grid = grid<User>(dataProvider) {
                    width = "100%"

                    // className = "show-admin-panel"

                    columnFor(User::username) {
                        setHeader("Имя пользователя")
                        setSortProperty(User::username.exp)
                    }
                    columnFor(User::role) {
                        setHeader("Роль")
                        setSortProperty(User::role.exp)
                    }

                    addColumn(ComponentRenderer<Button, User> { tr -> createEditButton(tr) }).apply {
                        flexGrow = 0; key = ""
                        isExpand = false
                    }

                }
                grid.height = "40px"
                grid.addThemeVariants(GridVariant.LUMO_NO_BORDER)
                grid.addThemeVariants(GridVariant.LUMO_COMPACT)
                setAlignSelf(FlexComponent.Alignment.CENTER, grid);

                p{}
                val grid1 = grid<User>(dataProvider) {
                    width = "100%"

                    // className = "show-admin-panel"

                    columnFor(User::username) {
                        setHeader("Имя пользователя")
                        setSortProperty(User::username.exp)
                    }
                    columnFor(User::role) {
                        setHeader("Роль")
                        setSortProperty(User::role.exp)
                    }

                    addColumn(ComponentRenderer<Button, User> { tr -> createEditButton(tr) }).apply {
                        flexGrow = 0; key = ""
                        isExpand = false
                    }

                }
                grid1.height = "40px"
                grid1.addThemeVariants(GridVariant.LUMO_NO_BORDER)
                grid1.addThemeVariants(GridVariant.LUMO_COMPACT)
                setAlignSelf(FlexComponent.Alignment.CENTER, grid1);

                p{}
               // if (showTur) {
                    val grid2 = grid<User>(dataProvider) {
                        width = "100%"

                        // className = "show-admin-panel"

                        columnFor(User::username) {
                            setHeader("Имя пользователя")
                            setSortProperty(User::username.exp)
                        }
                        columnFor(User::role) {
                            setHeader("Роль")
                            setSortProperty(User::role.exp)
                        }

                        addColumn(ComponentRenderer<Button, User> { tr -> createEditButton(tr) }).apply {
                            flexGrow = 0; key = ""
                            isExpand = false
                        }

                    }
                    grid2.height = "40px"
                    grid2.addThemeVariants(GridVariant.LUMO_NO_BORDER)
                    grid2.addThemeVariants(GridVariant.LUMO_COMPACT)
                    setAlignSelf(FlexComponent.Alignment.CENTER, grid2);
               // }
            }
        }

    }
    private fun createEditButton(user: User): Button =
        Button("").apply {
            icon = Icon(VaadinIcon.EDIT)
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            height = "22px"
            onClick {  }
        }

    private fun addTable() {
        panel.add(ServiceForm("name"))
    }


    private fun updateView() {

        dataProvider.setFilterText("AAAAAA")
        dataProvider.setSortFields(User::username.asc)
        //grid.dataProvider = dataProvider

    }
    init {
        updateView()
    }
}

class ServiceForm(name: String) : KComposite() {

    private val root = ui {
       verticalLayout(spacing = true, padding = false) {
           val grid = grid<User>() {
               width = "100%"
               columnFor(User::username) {
                   setHeader("XaXA")
                   setSortProperty(User::username.exp)
               }
               columnFor(User::role) {
                   setHeader("Mimo")
                   setSortProperty(User::role.exp)
               }

           }
           grid.height = "40px"
           grid.addThemeVariants(GridVariant.LUMO_NO_BORDER)
           grid.addThemeVariants(GridVariant.LUMO_COMPACT)
           setAlignSelf(FlexComponent.Alignment.CENTER, grid);



p{}



       }
    }
}

class TurView(name: String) : KComposite() {

    private val root = ui {
        verticalLayout(spacing = true, padding = false) {

            val v = verticalLayout { width = "100%"
                text("00000000000")
                br{}
                text("111111111111")
                br{}
                text("2222222222222")
                br{}
                text("33333333333")
                br{}
            }
            accordion {
                panel("Льготные категории") {
                    summary {
                        horizontalLayout(padding = false, spacing = false) {
                            style.set("padding", "5px")
                            text("Льготные категории")
                        }
                    }
                    content {
                        verticalLayout(padding = false, spacing = false)  {
                            style.set("background-color", "white")
                            add(v)
                        }
                    }

                }
                close()
            }
        }
    }
}