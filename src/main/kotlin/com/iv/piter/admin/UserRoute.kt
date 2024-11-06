package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.vokorm.asc
import com.github.vokorm.exp
import com.iv.piter.Toolbar
import com.iv.piter.security.User
import com.iv.piter.security.setFilterText
import com.iv.piter.toolbarView
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.shared.Tooltip
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin.setSortProperty
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider
import jakarta.annotation.security.RolesAllowed

@Route("user", layout = AdminLayout::class)
@PageTitle("Пользователи")
@RolesAllowed("Администратор")
class UserRoute : KComposite() {

    private lateinit var header: H5
    private lateinit var toolbar: Toolbar
    private lateinit var grid: Grid<User>
    private lateinit var mgrid: VirtualList<User>

    private val editorDialog = UserEditorForm.UserEditorDialog { updateView() }

    private val dataProvider = User.dataProvider

    private val root = ui {

        verticalLayout(true) {
            content { align(stretch, top) }
            setSizeFull()
            toolbar = toolbarView("Новый пользователь") {
                onSearch = { updateView() }
                onCreate = { editorDialog.createNew() }
            }
            header = h5()
            grid = grid(dataProvider) {
                className = "hide-admin-menu"
                isExpand = true
                width = "100%"

                columnFor(User::active, createUserActiveCheckboxRenderer()) {
                    isExpand = false
                    setHeader("Активен")
                    width = "10%"
                    isSortable = false
                }
                columnFor(User::username) {
                    setHeader("Имя пользователя")
                    width = "40%"
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

                element.themeList.add("row-dividers")
            }

            mgrid = virtualList {
                className = "hide-admin-panel"
                setRenderer(ComponentRenderer { row ->
                    val item = UserItem(row)
                    item.onSave = {
                        edit(row)
                    }
                    item
                })
            }

        }
    }

    init {
        updateView()
    }

    private fun createUserActiveCheckboxRenderer(): ComponentRenderer<Checkbox, User> =
        ComponentRenderer { user ->
            Checkbox(user.active).apply {
                // when the check box is changed, update the user and reload the grid
                addValueChangeListener {
                    user.active = it.value
                    user.save()
                    grid.dataProvider.refreshAll()
                }
            }
        }


    private fun createEditButton(user: User): Button =
        Button("").apply {
            icon = Icon(VaadinIcon.EDIT)
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            height = "22px"
            onClick { edit(user) }
        }

    private fun edit(user: User) {
        editorDialog.edit(user)
    }

    private fun updateView() {

        dataProvider.setFilterText(toolbar.searchText)
        if (toolbar.searchText.isNotBlank()) {
            header.text = "Поиск “${toolbar.searchText}”"
        } else {
            header.text = "Пользователь"
        }
        dataProvider.setSortFields(User::username.asc)
        grid.dataProvider = dataProvider
        mgrid.dataProvider = dataProvider
    }

}

class UserItem(val row: User) : KComposite() {
    val user: User get() = row
    var onSave: () -> Unit = {}
    val binder: Binder<User> = beanValidationBinder()

    private val root = ui {
        verticalLayout(spacing = false) {

            style.set("border-top", "gray solid 0.005em")
            style.set("border-radius", "2px")

            horizontalLayout {
                setWidthFull()
                checkBox("Активен") {
                    bind(binder).bind(User::active)
                    addValueChangeListener {
                        user.active = it.value
                        user.save()
                    }
                }

                horizontalLayout {
                    setWidthFull()
                    justifyContentMode = FlexComponent.JustifyContentMode.END

                    val editButton = button {
                        style.set("background-color", "transparent")
                        icon = VaadinIcon.EDIT.create()
                        width = "50px"
                        onClick {
                            onSave()
                        }
                    }
                    Tooltip.forComponent(editButton)
                        .withText("Редактировать")
                        .withPosition(Tooltip.TooltipPosition.TOP_START)
                }
            }
            textField("Имя пользователя") {
                setWidthFull()
                bind(binder).bind(User::username)
                isEnabled = false
            }
            textField("Роль") {
                setWidthFull()
                bind(binder).bind(User::role)
                isEnabled = false
            }
        }

    }

    init {
        binder.readBean(row)
        binder.validate()
    }

}
