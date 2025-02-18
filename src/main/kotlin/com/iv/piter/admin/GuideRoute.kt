package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.mvysny.kaributools.fetchAll
import com.github.vokorm.asc
import com.github.vokorm.exp
import com.iv.piter.ConfirmationDialog
import com.iv.piter.Constant
import com.iv.piter.Toolbar
import com.iv.piter.entity.Guide
import com.iv.piter.entity.setFilterText
import com.iv.piter.security.User
import com.iv.piter.security.setFilterText
import com.iv.piter.toolbarView
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.shared.Tooltip
import com.vaadin.flow.component.textfield.TextAreaVariant
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.dom.Element
import com.vaadin.flow.dom.ElementFactory
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin.setSortProperty
import eu.vaadinonkotlin.vaadin.vokdb.dataProvider
import io.netty.handler.codec.http.HttpHeaders.setHeader
import jakarta.annotation.security.RolesAllowed

@Route("guide", layout = AdminLayout::class)
@PageTitle("Гиды")
@RolesAllowed("Администратор")
class GuideRoute : KComposite() {

    private lateinit var header: H5
    private lateinit var toolbar: Toolbar
    private lateinit var grid: Grid<Guide>
    private lateinit var mgrid: VirtualList<Guide>

    private val editorDialog = GuideEditorForm.GuideEditorDialog { updateView() }

    private val dataProvider = Guide.dataProvider

    private val root = ui {

        verticalLayout(true) {
            content { align(stretch, top) }
            setSizeFull()
            toolbar = toolbarView("Новый гид") {
                onSearch = { updateView() }
                onCreate = { editorDialog.createNew() }
            }
            header = h5()
            grid = grid(dataProvider) {
                className = "hide-admin-menu"
                isExpand = true
                width = "100%"

                columnFor(Guide::active, createGuideActiveCheckboxRenderer()) {
                    isExpand = false
                    setHeader("Активен")
                    width = "10%"
                    isSortable = false
                }
                columnFor(Guide::firstname) {
                    setHeader("Имя")
                    width = "10%"
                    setSortProperty(Guide::firstname.exp)
                }

                columnFor(Guide::lastname) {
                    setHeader("Фамилия")
                    width = "30%"
                    setSortProperty(Guide::lastname.exp)
                }

                columnFor(Guide::phone) {
                    setHeader("Телефон")
                    setSortProperty(Guide::phone.exp)
                }

                addColumn(ComponentRenderer<Button, Guide> { tr -> createEditButton(tr) }).apply {
                    flexGrow = 0; key = ""
                    isExpand = false
                }

                element.themeList.add("row-dividers")
            }

            mgrid = virtualList {
                className = "hide-admin-panel"
                setRenderer(ComponentRenderer { row ->
                    val item = GuideItem(row)
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

    private fun createGuideActiveCheckboxRenderer(): ComponentRenderer<Checkbox, Guide> =
        ComponentRenderer { guide ->
            Checkbox(guide.active).apply {
                // when the check box is changed, update the guide and reload the grid
                addValueChangeListener {
                    guide.active = it.value
                    guide.save()
                    grid.dataProvider.refreshAll()
                }
            }
        }


    private fun createEditButton(guide: Guide): Button =
        Button("").apply {
            icon = Icon(VaadinIcon.EDIT)
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            height = "22px"
            onClick { edit(guide) }
        }

    private fun edit(guide: Guide) {
        editorDialog.edit(guide)
    }

    private fun updateView() {

        dataProvider.setFilterText(toolbar.searchText)
        if (toolbar.searchText.isNotBlank()) {
            header.text = "Поиск “${toolbar.searchText}”"
        } else {
            header.text = "Гид"
        }
        dataProvider.setSortFields(Guide::lastname.asc)
        grid.dataProvider = dataProvider
        mgrid.dataProvider = dataProvider
    }

}

class GuideItem(val row: Guide) : KComposite() {
    val guide: Guide get() = row
    var onSave: () -> Unit = {}
    val binder: Binder<Guide> = beanValidationBinder()

    private val root = ui {
        verticalLayout(spacing = false) {

            style.set("border-top", "gray solid 0.005em")
            style.set("border-radius", "2px")

            horizontalLayout {
                setWidthFull()
                checkBox("Активен") {
                    bind(binder).bind(Guide::active)
                    addValueChangeListener {
                        guide.active = it.value
                        guide.save()
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
            textField("Имя") {
                setWidthFull()
                bind(binder).bind(Guide::firstname)
                isEnabled = false
                className = "field_disable_text_color"
            }
            textField("Фамилия") {
                setWidthFull()
                bind(binder).bind(Guide::lastname)
                isEnabled = false
                className = "field_disable_text_color"
            }
            textField("Телефон") {
                setWidthFull()
                bind(binder).bind(Guide::phone)
                isEnabled = false
                className = "field_disable_text_color"
            }
        }

    }

    init {
        binder.readBean(row)
        binder.validate()
    }

}
