package com.iv.piter

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.footer
import com.github.mvysny.karibudsl.v23.header
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.shared.Registration

/**
 * A generic dialog for confirming or cancelling an action.
 */
internal class ConfirmationDialog : Dialog() {

    private lateinit var titleField: H5
    private lateinit var messageLabel: Div
    private lateinit var extraMessageLabel: Div
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button
    private var registrationForConfirm: Registration? = null

    init {
        addClassNames("confirm-dialog")
        isCloseOnEsc = true
        isCloseOnOutsideClick = false

        header {
            titleField = h5()
        }
        div {
            // labels
            className = "confirm-text"
            messageLabel = div()
            extraMessageLabel = div()
        }
        footer {
            confirmButton = button {
                addClickListener { close() }
                isAutofocus = true
            }
            cancelButton = button("Отмена") {
                addClickListener {
                    close()
                }
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            }
        }
    }

    /**
     * Opens the confirmation dialog with given [title].
     *
     * The dialog will display the given title and message(s), then call
     * [confirmHandler] if the Confirm button is clicked.
     * @param message Detail message (optional, may be empty)
     * @param additionalMessage Additional message (optional, may be empty)
     * @param actionName The action name to be shown on the Confirm button
     * @param isDisruptive True if the action is disruptive, such as deleting an item
     */
    fun open(title: String, message: String = "", additionalMessage: String = "",
             actionName: String, isDisruptive: Boolean, isInform: Boolean, isImportant: Boolean, confirmHandler: () -> Unit) {
        titleField.text = title
        if (isImportant) messageLabel.className = "error_text"
        messageLabel.text = message

        extraMessageLabel.text = additionalMessage
        if (isInform) confirmButton.isVisible = false
        confirmButton.text = actionName

        registrationForConfirm?.remove()
        registrationForConfirm = confirmButton.addClickListener { confirmHandler() }
        if (isDisruptive) {
            confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        open()
    }


}
