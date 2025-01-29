package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.iv.piter.ConfirmationDialog
import com.iv.piter.EditorDialogFrame
import com.iv.piter.EditorForm
import com.iv.piter.entity.Customer
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.data.binder.Binder
import java.time.LocalDateTime

class CustomerEditorForm(val customer: Customer) : FormLayout(), EditorForm<Customer> {
    private val isEditing get() = customer.id != null
    override val itemType: String get() = "заказчика"
    override val binder: Binder<Customer> = beanValidationBinder()
    private var pwd: PasswordField = PasswordField()
    override var isEdit:Boolean = pwd.isVisible

    init {
        maxWidth = "500px"
        responsiveSteps {
            "0"(1); "320px"(1); "480px"(1)
            "580px"(1)
        }
        textField("Имя входа") {
            bind(binder)
                .trimmingConverter()
                .withValidator(
                    { isNameUnique(value) }, "данное имя уже существует"
                )
                .asRequired()
                .bind(Customer::username)
        }
        textField("Имя") {
            bind(binder)
                .trimmingConverter()
                .asRequired()
                .bind(Customer::firstname)
        }
        textField("Фамилия") {
            bind(binder)
                .trimmingConverter()
                .asRequired()
                .bind(Customer::lastname)
        }
        textField("Телефон") {
            bind(binder)
                .trimmingConverter()
                .asRequired()
                .bind(Customer::phone)
        }

        horizontalLayout(padding = false, spacing = false) {

//            pwd = passwordField("Пароль") {
//
//                bind(binder)
//                    .withValidator(
//                        { it.length >= 5 },
//                        "не менее 5 символов"
//                    ).bind(Customer::getHashedPassword, Customer::setPassword)
//            }

            button {
                style.set("margin-left", "auto")
                style.set("background-color", "transparent")
                pwd.isVisible = customer.id != null
                setPwdField( this, pwd)
                isEdit = pwd.isVisible
                if (!isEditing) {
                    isVisible = false
                }
                onClick {
                    setPwdField(this, pwd)
                    isEdit = pwd.isVisible
                }
            }
        }
    }

    private fun isNameUnique(username: String?): Boolean {
        if (username.isNullOrBlank()) return true
        if ((customer.username ?: "") == username) return true
        return !Customer.existsWithName(username)
    }

    fun setPwdField(button: Button, field: PasswordField) {

        if (!field.isVisible) {
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


    /**
     * Opens dialogs for editing [Customer] objects.
     * @property onCustomerChanged called when a customer has been created/edited/deleted.
     */
    class CustomerEditorDialog(private val onCustomerChanged: (Customer) -> Unit) {

        fun maybeDelete(frame: EditorDialogFrame<Customer>?, item: Customer) {

            val orderCount = 1L

            if (orderCount == 0L) {
                // delete(frame, item)
            } else {
                ConfirmationDialog().open(
                    "Удалить данные пользователя “${item.username}”?",
                    "Число заказов, ассоциированных с данным транспортом, больше 0.",
                    "Удаление данного пользователя приведет к наличию неопределенных данных.",
                    "Удалить",
                    true,
                    false,
                    false
                ) {
                    if (frame != null) delete(frame, item)
                }
            }
        }

        private fun delete(frame: EditorDialogFrame<Customer>, item: Customer) {
            item.delete()
            Notification.show("Пользователь успешно удален.", 3000, Notification.Position.BOTTOM_START)
            frame.close()
            onCustomerChanged(item)
        }

        fun createNew() {
            edit(Customer())
        }

        fun edit(customer: Customer) {

            val frame = EditorDialogFrame(CustomerEditorForm(customer))

            frame.onSaveItem = {
                val creating: Boolean = customer.id == null
                customer.updated = LocalDateTime.now()
                if (creating) {
                    customer.created = LocalDateTime.now()
                }
//                else {
//                    if (!frame.editorForm.isEdit) {
//                        customer.hashedPassword = Customer.getById(customer.id!!).hashedPassword
//                    }
//                }
                customer.save()
                val op: String = if (creating) "добавлен" else "сохранен"
                val n = Notification.show("Заказчик успешно ${op}.", 3000, Notification.Position.TOP_END)
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                onCustomerChanged(customer)
                frame.closeDialog()
            }
            frame.onDeleteItem = { item -> maybeDelete(frame, item) }
            frame.open(customer, customer.id == null)
        }
    }

}
