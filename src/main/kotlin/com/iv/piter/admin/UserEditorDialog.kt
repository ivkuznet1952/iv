package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.iv.piter.ConfirmationDialog
import com.iv.piter.Constant
import com.iv.piter.EditorDialogFrame
import com.iv.piter.EditorForm
import com.iv.piter.entity.Transport
import com.iv.piter.security.User
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.data.binder.Binder
import java.time.LocalDateTime

/**
 * A form for editing [User] objects.
 */
class UserEditorForm(val user: User) : FormLayout(), EditorForm<User> {
    private val isEditing get() = user.id != null
    override val itemType: String get() = "пользователя"
    override val binder: Binder<User> = beanValidationBinder()
    private var pwd: PasswordField = PasswordField()
    override var isEdit:Boolean = pwd.isVisible

    init {

        if (user.id == null && Constant.ROLES.isNotEmpty()) user.role = Constant.ROLES[0]

            responsiveSteps {
                "0"(1); "320px"(1); "480px"(1)
                "780px"(1)
            }
            //setWidthFull()
            textField("Имя") {
               // width = "100%"
                bind(binder)
                    .trimmingConverter()
                    .withValidator(
                        { isNameUnique(value) }, "данное имя уже существует"
                    ).asRequired()
                    .bind(User::username)
            }
            comboBox<String>("Роль") {
               // width = "100%"
                isAllowCustomValue = false
                setItems(Constant.ROLES)
                bind(binder)
                    .trimmingConverter().asRequired()
                    .bind(User::role)
            }

        horizontalLayout(padding = false, spacing = false) {

            pwd = passwordField("Пароль") {

                bind(binder)
                    .withValidator(
                        { it.length >= 5 },
                        "не менее 5 символов"
                    ).bind(User::getHashedPassword, User::setPassword)
            }

            button {
                style.set("margin-left", "auto")
                style.set("background-color", "transparent")
                pwd.isVisible = user.id != null
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

    private fun isNameUnique(name: String?): Boolean {
        if (name.isNullOrBlank()) return true
        if ((user.username ?: "") == name) return true
        return !User.existsWithName(name)
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
     * Opens dialogs for editing [User] objects.
     * @property onUserChanged called when a user has been created/edited/deleted.
     */
    class UserEditorDialog(private val onUserChanged: (User) -> Unit) {

        fun maybeDelete(frame: EditorDialogFrame<User>?, item: User) {

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

        private fun delete(frame: EditorDialogFrame<User>, item: User) {
            item.delete()
            Notification.show("Пользователь успешно удален.", 3000, Notification.Position.BOTTOM_START)
            frame.close()
            onUserChanged(item)
        }

        fun createNew() {
            edit(User())
        }

        fun edit(user: User) {

            val frame = EditorDialogFrame(UserEditorForm(user))

            frame.onSaveItem = {
                val creating: Boolean = user.id == null
                user.updated = LocalDateTime.now()
                if (creating) {
                    user.created = LocalDateTime.now()
                } else {
                    if (!frame.editorForm.isEdit) {
                        user.hashedPassword = User.getById(user.id!!).hashedPassword
                    }
                }
                user.save()
                val op: String = if (creating) "добавлен" else "сохранен"
                val n = Notification.show("Пользователь успешно ${op}.", 3000, Notification.Position.TOP_END)
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                onUserChanged(user)
                frame.closeDialog()
            }
            frame.onDeleteItem = { item -> maybeDelete(frame, item) }
            frame.open(user, user.id == null)
        }
    }

}
