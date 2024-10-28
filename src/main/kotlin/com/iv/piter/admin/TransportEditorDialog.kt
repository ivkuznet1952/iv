package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.iv.piter.ConfirmationDialog
import com.iv.piter.EditorDialogFrame
import com.iv.piter.EditorForm
import com.iv.piter.entity.Transport

import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.StringLengthValidator


/**
 * A form for editing [Transport] objects.
 */
class TransportEditorForm(val transport: Transport) : FormLayout(), EditorForm<Transport> {
    //private val isEditing get() = transport.id != null
    override var isEdit:Boolean = true
    override val itemType: String get() = "транспорт"
    override val binder: Binder<Transport> = beanValidationBinder()

    init {
        responsiveSteps {
            "0"(1); "320px"(1); "480px"(1)
            "580px"(1)
        }

        textField("Наименование") {
            minWidth = "250px"
            bind(binder)
                .trimmingConverter()
                .withValidator(
                    StringLengthValidator(
                        "Наименование не задано",
                        0, null
                    )
                )
                .bind(Transport::name)
        }
    }

    /**
     * Opens dialogs for editing [Transport] objects.
     * @property onTransportChanged called when a user has been created/edited/deleted.
     */
    class TransportEditorDialog(private val onTransportChanged: (Transport) -> Unit) {
        private fun maybeDelete(frame: EditorDialogFrame<Transport>, item: Transport) { // TODO

            val orderCount = 0L
                //item.id?.let { TOrder.getTotalCountForTOrdersInTransport(it) }

            if (orderCount == 0L) {
               // delete(frame, item)
            } else {
                ConfirmationDialog().open(
                    "Удалить данные транспорта “${item.name}”?",
                    "Число заказов, ассоциированных с данным транспортом, больше 0.",
                    "Удаление данного транспорта приведет к наличию неопределенных данных.",
                    "Удалить",
                    true,
                    false,
                    false
                ) {
                    delete(frame, item)
                }
            }
         }

        private fun delete(frame: EditorDialogFrame<Transport>, item: Transport) {
            item.delete()
            Notification.show("Транспорт успешно удален.", 3000, Notification.Position.BOTTOM_START)
            frame.close()
            onTransportChanged(item)
        }

        fun createNew() {
            edit(Transport())
        }

        fun edit(transport: Transport) {

            val frame = EditorDialogFrame(TransportEditorForm(transport))
            frame.onSaveItem = {
                val creating: Boolean = transport.id == null
                transport.save()
                val op: String = if (creating) "добавлен" else "сохранен"
                val n = Notification.show("Транспорт успешно ${op}.", 3000, Notification.Position.TOP_END)
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                onTransportChanged(transport)
                frame.closeDialog()
            }
            frame.onDeleteItem = { item -> maybeDelete(frame, item) }
            frame.open(transport, transport.id == null)
        }
    }

}

