package com.iv.piter

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.footer
import com.github.mvysny.karibudsl.v23.header
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.data.binder.Binder
import java.io.Serializable

/**
 * The editor form which edits beans of type [T]. This class is a Vaadin component
 * (usually a [FormLayout]) which contains the fields. All fields are also referenced
 * via [binder]. Nest this into the [EditorDialogFrame].
 */
interface EditorForm<T : Serializable> {
    /**
     * The displayable name of the item type [T].
     */
    val itemType: String
    /**
     * All form fields are registered to this binder.
     */
    val binder: Binder<T>

    var hasError: Boolean
        get() = false
        set(value) = Unit

}

/**
 * A dialog frame for dialogs adding, editing or deleting items.
 *
 * Users are expected to:
 *  * Set [form] with a proper implementation.
 *  * Set [onSaveItem] and [onDeleteItem]
 *  * Call [open] to show the dialog.
 * @param T the type of the item to be added, edited or deleted
 * @property form the form itself
 */
class EditorDialogFrame<T : Serializable>(private val form: EditorForm<T>) : Dialog() {

    private lateinit var titleField: H4
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    lateinit var deleteButton: Button

    /**
     * The item currently being edited.
     */
    var currentItem: T? = null
        private set

    /**
     * Callback after the edited item has been saved/created. The dialog frame is closed automatically.
     */
    lateinit var onSaveItem: (item: T)->Unit

    /**
     * Callback to delete the edited item. Should open confirmation dialog (or delete the item directly if possible).
     * Note that the frame is not closed automatically and must be closed manually.
     */
    lateinit var onDeleteItem: (item: T)->Unit

    init {
        isCloseOnEsc = true
        isCloseOnOutsideClick = false

        header {
            titleField = h4()
        }
        add(form as Component)
        footer {
            saveButton = button("Сохранить") {
                isAutofocus = true
                setPrimary()
            }
            cancelButton = button("Отмена") {
                addClickListener { closeDialog() }
            }
            deleteButton = button("Удалить") {
                addThemeVariants(ButtonVariant.LUMO_ERROR)
                addClickListener { onDeleteItem(currentItem!!) }
            }
        }
       if (form.hasError) {
           saveButton.onEnabledStateChanged(false)
       }
    }

    init {
        saveButton.addClickListener { saveClicked() }
    }

    /**
     * Opens given item for editing in this dialog.
     *
     * @param item The item to edit; it may be an existing or a newly created instance
     * @param creating if true, the item is being created; if false, the item is being edited.
     */
    fun open(item: T, creating: Boolean) {
        currentItem = item

        val operation = if (creating) "Добавить" else "Изменить"
        titleField.text = "$operation ${form.itemType}"
        form.binder.readBean(currentItem)

        deleteButton.isVisible = !creating
        saveButton.text = if (creating) "Добавить" else "Сохранить"
        open()
    }

    private fun saveClicked() {
        // form.binder.isValid won't set `isInvalid` and `errorMessage` of invalid components - avoid.
        // use form.binder.validate() instead.
        if (form.binder.validate().isOk && form.binder.writeBeanIfValid(currentItem!!)) {
            onSaveItem(currentItem!!)
        } else {
            val n = Notification.show("Данные имеют ошибки", 3000, Notification.Position.TOP_END)
            n.addThemeVariants(NotificationVariant.LUMO_ERROR)
        }
    }
    public fun closeDialog() {
        close()
    }
}
