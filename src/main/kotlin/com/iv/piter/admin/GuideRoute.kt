package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.fetchAll
import com.iv.piter.ConfirmationDialog
import com.iv.piter.Constant
import com.iv.piter.Toolbar
import com.iv.piter.entity.Guide
import com.iv.piter.entity.setFilterText
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

@Route("guide", layout = AdminLayout::class)
@PageTitle("Гиды")
@RolesAllowed("ROLE_ADMIN")
class GuideRoute : KComposite() {

    private lateinit var header: H5
    private lateinit var toolbar: Toolbar
    private lateinit var grid: VirtualList<Guide>
    private val dataProvider = Guide.dataProvider

    private val root = ui {
        verticalLayout(false, spacing = false) {
            content { align(stretch, top) }
            setWidthFull()
            setHeightFull()
            header = h5 {
                setId("header")
            }
            br{}
            toolbar = toolbarView("Добавить") {
                onSearch = { updateView() }
                onCreate = { createNew() }
            }
           br{}
            grid = virtualList {
                var ind = 0
                setRenderer(ComponentRenderer<GuideItem, Guide> { row ->
                    val item = GuideItem(row, ind)
                    item.onDelete = {
                        var isDelete = false
                        if (row.id != null) isDelete = maybeDelete(row)
                        if (isDelete) {
                            updateView()
                            val n = Notification.show("Данные успешно удалены.", 3000, Notification.Position.TOP_END)
                            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                        }
                    }
                    item.onSave = {

                    }
                    ind++
                    item
                })
            }
        }
    }

    init {
        setId("Guide")
        updateView()
    }

    private fun createNew() {
        val dp: ListDataProvider<Guide> = ListDataProvider(Guide.dataProvider.fetchAll())
        val guide: Guide = Guide()
        guide.firstname = ""
        dp.items.add(guide)
        grid.dataProvider = dp
    }

    private fun updateView() {
        dataProvider.setFilterText(toolbar.searchText)
        if (toolbar.searchText.isNotBlank()) {
            header.text = "Поиск “${toolbar.searchText}”"
        } else {
            header.text = "Данные гида"
        }
        grid.dataProvider = dataProvider
    }

    private fun maybeDelete(item: Guide): Boolean {
//        val turCount = Tur.findAllBy { Tur::direction_id eq item.id }.count()
//        if (turCount == 0) {
//            item.delete()
//        } else {
        ConfirmationDialog().open(
            "Удалить данные “${item.lastname} ${item.firstname} ”?",
            "С данным гидом ассоцированы несколько заказов. Удаление невозможно!",
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
 * Shows a single row stripe with information about a single [Guide].
 */
class GuideItem(val row: Guide, ind: Int) : KComposite() {
    val user: Guide get() = row
    var onDelete: () -> Unit = {}
    var onSave: () -> Unit = {}
    val binder: Binder<Guide> = beanValidationBinder()
//    private var pwd: TextField = TextField()
//    private var confirmpwd: TextField = TextField()

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

//            textField("Имя пользователя") {
//                bind(binder)
//                    .trimmingConverter().asRequired("Значение не задано").bind(User::username)
//            }
//            textField("Роль") {
//                bind(binder)
//                    .trimmingConverter().asRequired("Значение не задано").bind(User::roles)
//            }
//            checkBox ("Активен") {
//                bind(binder).bind(User::active)
//            }

            // svg icon
            horizontalLayout(padding = false, spacing = false) {

                val img = Image(Constant.SAVE_ICON, "save")
                img.width = "16px"
                val save = iconButton(img) {
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    style.set("margin-left","auto")
                   // onClick { onSave(pwd.value, confirmpwd.value) }
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


        }
    }

    init {
        binder.readBean(row)
        binder.validate()
    }

    override fun toString(): String = "UserItem($user)"
}
