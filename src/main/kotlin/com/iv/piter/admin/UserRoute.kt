package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.fetchAll
import com.iv.piter.ConfirmationDialog
import com.iv.piter.Constant
import com.iv.piter.Toolbar
import com.iv.piter.security.User
import com.iv.piter.security.setFilterText
import com.iv.piter.toolbarView
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.shared.Tooltip
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.binder.ValidationException
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider
import jakarta.annotation.security.RolesAllowed
import java.time.LocalDateTime


@Route("user", layout = AdminLayout::class)
@PageTitle("Пользователи")
@RolesAllowed("ROLE_ADMIN")
class UserRoute : KComposite() {

    private lateinit var header: H5
    private lateinit var toolbar: Toolbar
    private lateinit var grid: VirtualList<User>
    private val dataProvider = User.dataProvider

    private val root = ui {
        verticalLayout(false, spacing = false) {
            content { align(stretch, top) }
            setWidthFull()
            setHeightFull()
            header = h5 {
                style.set("margin-left","10px")
                setId("header")
            }
            toolbar = toolbarView("Добавить") {
                onSearch = { updateView() }
                onCreate = { createNew() }
            }
            grid = virtualList {
                var ind = 0
                setRenderer(ComponentRenderer { row ->
                    val item = UserItem(row, ind)
                    item.onDelete = {
                        var isDelete = false
                        if (row.id != null) isDelete = maybeDelete(row)
                        if (isDelete) {
                            updateView()
                            val n = Notification.show("Данные успешно удалены.", 3000, Notification.Position.TOP_END)
                            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                        }
                    }
                    item.onSave = { isPwdEdit ->
                        try {
                            item.binder.writeBean(row)
                            row.updated = LocalDateTime.now()
                            if (row.id == null) {
                                row.created = LocalDateTime.now()
                            } else {
                                if (!isPwdEdit) {
                                    row.hashedPassword = User.getById(row.id!!).hashedPassword
                                }
                            }
                            row.save()
                            val n = Notification.show("Данные успешно сохранены.", 3000, Notification.Position.TOP_END)
                            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                            updateView()
                        } catch (ve: ValidationException) {
                            val n = Notification.show(
                                "Данные имеют ошибки и не сохранены.",
                                3000,
                                Notification.Position.TOP_END
                            )
                            n.addThemeVariants(NotificationVariant.LUMO_ERROR)
                        }
                    }
                    ind++
                    item
                })
            }
        }
    }

    init {
        setId("User")
        updateView()
    }

    private fun createNew() {
        val dp: ListDataProvider<User> = ListDataProvider(User.dataProvider.fetchAll())
        val user = User()
        user.username = ""
        user.role = "ROLE_MANAGER"
        user.hashedPassword = ""
        dp.items.add(user)
        grid.dataProvider = dp
    }

    private fun updateView() {
        dataProvider.setFilterText(toolbar.searchText)
        if (toolbar.searchText.isNotBlank()) {
            header.text = "Поиск “${toolbar.searchText}”"
        } else {
            header.text = "Данные пользователей"
        }
        grid.dataProvider = dataProvider
    }

    private fun maybeDelete(item: User): Boolean {
        val userOrderCount = 1 // TODO
        if (userOrderCount == 0) {
            item.delete()
        } else {
            ConfirmationDialog().open(
                "Удалить данные “${item.username} ”?",
                "С данным пользователем ассоцированы несколько заказов. Удаление невозможно!",
                "",
                "",
                true,
                true,
                false
            ) {

            }
            return false
        }
        return true
    }
}


/**
 * Shows a single row stripe with information about a single [User].
 */
class UserItem(val row: User, ind: Int) : KComposite() {
    val user: User get() = row
    var onDelete: () -> Unit = {}
    var onSave: (isPwdEdit: Boolean) -> Unit = { }
    val binder: Binder<User> = beanValidationBinder()
    var pwd: PasswordField = PasswordField()

    private val root = ui {

        formLayout(classNames = "") {
            this.style.set("padding", "5px")
            style.set("border-bottom", "gray solid 0.005em")
            if (ind == 0) style.set("border-top", "gray solid 0.005em")
            style.set("border-radius", "2px")

            responsiveSteps {
                "0"(1); "320px"(1); "480px"(2)
                "780px"(8)
            }
            setWidthFull()

            checkBox("Активен") {
                bind(binder).bind(User::active)
            }

            textField("Имя пользователя") {
                colspan = 2
                bind(binder).trimmingConverter().withValidator(
                    { isNameUnique(value) }, "данное имя уже существует"
                ).asRequired().bind(User::username)
            }
            //
            comboBox<String>("Роль") {
                colspan = 2
                isAllowCustomValue = false
                setItems(Constant.ROLES)

                bind(binder)
                    .trimmingConverter().asRequired()
                    .bind(User::role)
            }

            horizontalLayout(padding = false, spacing = false) {
                colspan = 2

                pwd = passwordField("Пароль") {
                    isVisible = false
                    bind(binder)
                        .withValidator(
                            { it.length >= 5 },
                            "не менее 5 символов"
                        ).bind(User::getHashedPassword, User::setPassword)
                }

                button {
                    style.set("margin-left", "auto")
                    style.set("background-color", "transparent")

                    setPwdField(row.id == null, this, pwd)
                    onClick {
                        setPwdField(!pwd.isVisible, this, pwd)
                    }
                }


            }
            // buttons
            horizontalLayout(padding = false, spacing = false) {

                colspan = 1
                val img = Image(Constant.SAVE_ICON, "save")
                img.width = "16px"
                val save = iconButton(img) {
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    style.set("margin-left", "auto")
                    onClick { onSave(pwd.isVisible) }
                }

                Tooltip.forComponent(save)
                    .withText("Сохранить")
                    .withPosition(Tooltip.TooltipPosition.TOP_START)

                val imgDel = Image(Constant.DELETE_ICON, "delete")
                imgDel.width = "16px"
                val deleteButton = iconButton(imgDel) {
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    onClick { onDelete() }
                }

                Tooltip.forComponent(deleteButton)
                    .withText("Удалить")
                    .withPosition(Tooltip.TooltipPosition.TOP_START)
            }

        }
    }

// init
    init {
        binder.readBean(row)
        binder.validate()
    }

    private fun isNameUnique(name: String?): Boolean {
        if (name.isNullOrBlank()) return true
        if (user?.username ?: "" == name) return true
        return !User.existsWithName(name)
    }


    fun setPwdField(isEdit: Boolean, button: Button, field: PasswordField) {
        if (isEdit) {
            field.value = ""
            button.icon = VaadinIcon.CLOSE.create()
            button.text = ""
            field.isVisible = true
            field.isExpand = true
        } else {
            button.text = "изменить пароль"
            field.isVisible = false
            button.icon = VaadinIcon.PENCIL.create()
            field.isExpand = false
        }
    }

    override fun toString(): String = "UserItem($user)"
}
