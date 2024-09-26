package com.iv.piter.admin

//import com.apple.laf.AquaButtonBorder
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.fetchAll
import com.iv.piter.ConfirmationDialog
import com.iv.piter.Constant
import com.iv.piter.Toolbar
import com.iv.piter.security.User
import com.iv.piter.security.setFilterText
import com.iv.piter.toolbarView
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.shared.Tooltip
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider
import jakarta.annotation.security.RolesAllowed


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
            toolbar = toolbarView("Добавить") {
//                setWidthFull()
                onSearch = { updateView() }
                onCreate = { createNew() }
            }
            header = h5 {
                setId("header")
            }
            grid = virtualList {
                var ind = 0
                setRenderer(ComponentRenderer<UserItem, User> { row ->
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
                    item.onSave = {pwd: String, confirm_pwd: String ->

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
        val user: User = User()
        user.username = ""
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
//        val turCount = Tur.findAllBy { Tur::direction_id eq item.id }.count()
//        if (turCount == 0) {
//            item.delete()
//        } else {
        ConfirmationDialog().open(
            "Удалить данные “${item.username} ”?",
            "С данным пользователем ассоцированы несколько заказов. Удаление невозможно!",
            "",
            "",
            true,
            true,
            false
        ) {}
        //      }
        return false
    }
}


/**
 * Shows a single row stripe with information about a single [User].
 */
class UserItem(val row: User, ind: Int) : KComposite() {
    val user: User get() = row
    var onDelete: () -> Unit = {}
    var onSave: (pwd: String, confirm_pwd: String) -> Unit = { s: String, s1: String -> }
    val binder: Binder<User> = beanValidationBinder()
    private var pwd: TextField = TextField()
    private var confirmpwd: TextField = TextField()

        private val root = ui {

        formLayout(classNames = "") {
            //style.set("padding", "13px")
            style.set("border-bottom", "gray solid 0.005em")
            if (ind == 0) style.set("border-top", "gray solid 0.005em")
            style.set("border-radius", "2px")

            responsiveSteps {
                "0"(1); "320px"(1); "480px"(2)
                "780px"(4)
            }
            setWidthFull()

            textField("Имя пользователя") {
                bind(binder)
                    .trimmingConverter().asRequired("Значение не задано").bind(User::username)
            }
            textField("Роль") {
                bind(binder)
                    .trimmingConverter().asRequired("Значение не задано").bind(User::roles)
            }
            checkBox ("Активен") {
                bind(binder).bind(User::active)
            }

            // svg icon
            horizontalLayout(padding = false, spacing = false) {

                val img = Image(Constant.SAVE_ICON, "save")
                img.width = "16px"
                val save = iconButton(img) {
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    style.set("margin-left","auto")
                    onClick { onSave(pwd.value, confirmpwd.value) }
                }
                Tooltip.forComponent(save)
                    .withText("Сохранить")
                    .withPosition(Tooltip.TooltipPosition.TOP_START)

                val deleteButton = button {
                    icon = VaadinIcon.TRASH.create()
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    style.set("color", "white")
                    style.set("margin-left","10px")
                    onClick { onDelete() }
                }
                Tooltip.forComponent(deleteButton)
                    .withText("Удалить")
                    .withPosition(Tooltip.TooltipPosition.TOP_START)
            }


            pwd =  textField("Пароль(не менее 5 симв.)") {
                isEnabled = false
            }
            confirmpwd = textField("Подтверждение пароля") {
                isEnabled = false
            }
            checkBox ("Изменить пароль") {
                onClick {
                    pwd.isEnabled = value
                    confirmpwd.isEnabled = value
                }
            }

    }
}

init {
    binder.readBean(row)
    binder.validate()
}

override fun toString(): String = "UserItem($user)"
}
