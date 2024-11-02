package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.virtualList
import com.github.vokorm.asc
import com.github.vokorm.exp
import com.iv.piter.Toolbar
import com.iv.piter.entity.Customer
import com.iv.piter.entity.setFilterText
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

@Route("customer", layout = AdminLayout::class)
@PageTitle("Заказчики")
@RolesAllowed("Администратор")
class CustomerRoute : KComposite() {

    private lateinit var header: H5
    private lateinit var toolbar: Toolbar
    private lateinit var grid: Grid<Customer>
    private lateinit var mgrid: VirtualList<Customer>

    private val editorDialog = CustomerEditorForm.CustomerEditorDialog { updateView() }

    private val dataProvider = Customer.dataProvider

    private val root = ui {

        verticalLayout(true) {
            content { align(stretch, top) }
            toolbar = toolbarView("Новый заказчик") {
                onSearch = { updateView() }
                onCreate = { editorDialog.createNew() }
            }
            header = h5()
            grid = grid(dataProvider) {
                className = "hide-admin-menu"
                isExpand = true
                width = "100%"

                columnFor(Customer::active, createCustomerActiveCheckboxRenderer()) {
                    isExpand = false
                    setHeader("Активен")
                    width = "10%"
                    isSortable = false
                }

                columnFor(Customer::username) {
                    setHeader("Имя входа")
                    width = "10%"
                    setSortProperty(Customer::username.exp)
                }

                columnFor(Customer::firstname) {
                    setHeader("Имя")
                    width = "10%"
                    setSortProperty(Customer::firstname.exp)
                }

                columnFor(Customer::lastname) {
                    setHeader("Фамилия")
                    width = "20%"
                    setSortProperty(Customer::lastname.exp)
                }

                columnFor(Customer::phone) {
                    setHeader("Телефон")
                }
                addColumn(ComponentRenderer<Button, Customer> { tr -> createEditButton(tr) }).apply {
                    flexGrow = 0; key = ""
                    isExpand = false
                }

                element.themeList.add("row-dividers")
            }

            mgrid = virtualList {
                className = "hide-admin-panel"
                setRenderer(ComponentRenderer { row ->
                    val item = CustomerItem(row)
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

    private fun createCustomerActiveCheckboxRenderer(): ComponentRenderer<Checkbox, Customer> =
        ComponentRenderer { customer ->
            Checkbox(customer.active).apply {
                // when the check box is changed, update the customer and reload the grid
                addValueChangeListener {
                    customer.active = it.value
                    customer.save()
                    grid.dataProvider.refreshAll()
                }
            }
        }


    private fun createEditButton(customer: Customer): Button =
        Button("").apply {
            icon = Icon(VaadinIcon.EDIT)
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            height = "22px"
            onClick { edit(customer) }
        }

    private fun edit(customer: Customer) {
        editorDialog.edit(customer)
    }

    private fun updateView() {

        dataProvider.setFilterText(toolbar.searchText)
        if (toolbar.searchText.isNotBlank()) {
            header.text = "Поиск “${toolbar.searchText}”"
        } else {
            header.text = "Заказчик"
        }
        dataProvider.setSortFields(Customer::username.asc)
        grid.dataProvider = dataProvider
        mgrid.dataProvider = dataProvider
    }

}

class CustomerItem(val row: Customer) : KComposite() {
    private val customer: Customer get() = row
    var onSave: () -> Unit = {}
    val binder: Binder<Customer> = beanValidationBinder()

    private val root = ui {
        verticalLayout(spacing = false) {

            style.set("border-top", "gray solid 0.005em")
            style.set("border-radius", "2px")

            horizontalLayout {
                setWidthFull()
                checkBox("Активен") {
                    bind(binder).bind(Customer::active)
                    addValueChangeListener {
                        customer.active = it.value
                        customer.save()
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
                bind(binder).bind(Customer::firstname)
                isEnabled = false
            }
            textField("Фамилия") {
                setWidthFull()
                bind(binder).bind(Customer::lastname)
                isEnabled = false
            }
            textField("Телефон") {
                setWidthFull()
                bind(binder).bind(Customer::phone)
                isEnabled = false
            }
        }

    }

    init {
        binder.readBean(row)
        binder.validate()
    }

}
