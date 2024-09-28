package com.iv.piter

import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.div
import com.github.mvysny.karibudsl.v10.h5
import com.github.mvysny.karibudsl.v23.footer
import com.github.mvysny.karibudsl.v23.header
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.shared.Registration

internal class ConfirmationDialogWithCancel : Dialog() {

    private lateinit var titleField: H5
    private lateinit var messageLabel: Div
    private lateinit var extraMessageLabel: Div
    private lateinit var callbackconfirmButton: Button
    private lateinit var callbackcancelButton: Button
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
            callbackconfirmButton = button {
                addClickListener { close() }
                isAutofocus = true
            }
            callbackcancelButton = button("Отмена") {
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
             actionName: String, isDisruptive: Boolean, isInform: Boolean, isImportant: Boolean, confirmHandler: (b: Boolean) -> Unit) {

        titleField.text = title
        if (isImportant) messageLabel.className = "error_text"
        messageLabel.text = message

        extraMessageLabel.text = additionalMessage
        if (isInform) callbackconfirmButton.isVisible = false
        callbackconfirmButton.text = actionName

        registrationForConfirm?.remove()
        registrationForConfirm = callbackconfirmButton.addClickListener { confirmHandler(true) }
        registrationForConfirm = callbackcancelButton.addClickListener { confirmHandler(false) }
        if (isDisruptive) {
            callbackconfirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        open()
    }

}
