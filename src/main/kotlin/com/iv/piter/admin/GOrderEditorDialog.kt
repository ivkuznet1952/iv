package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.iv.piter.EditorDialogFrame
import com.iv.piter.EditorForm
import com.iv.piter.entity.GOrder
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.StringLengthValidator

class GOrderEditorForm(val gorder: GOrder) : FormLayout(), EditorForm<GOrder> {

    override var isEdit: Boolean = true
    override val itemType: String get() = "заказ"
    override val binder: Binder<GOrder> = beanValidationBinder()

    init {
        responsiveSteps {
            "0"(1); "320px"(1); "380px"(2)
            "480px"(2)
        }

        textField("Комментарий (время и откуда забирать, др.") {
            minWidth = "280px"
            bind(binder)
                .trimmingConverter()
                .withValidator(
                    StringLengthValidator(
                        "Коментарий",
                        0, null
                    )
                )
                .bind(GOrder::comment)
        }
        integerField("Кол-во туристов") {
            maxWidth = "180px"
            bind(binder).bind(GOrder::num)
        }
    }


    /**
     * Opens dialogs for editing [GOrder] objects.
     * @property onGOrderChanged called when a user has been created/edited/deleted.
     */
    class GOrderEditorDialog(private val onGOrderChanged: (GOrder) -> Unit) {
        private fun maybeDelete(frame: EditorDialogFrame<GOrder>, item: GOrder) { // TODO

        }

        private fun delete(frame: EditorDialogFrame<GOrder>, item: GOrder) {
            item.delete()
            Notification.show("Заказ успешно удален.", 3000, Notification.Position.BOTTOM_START)
            frame.close()
            onGOrderChanged(item)
        }

        fun createNew() {
            edit(GOrder())
        }

        fun edit(gorder: GOrder) {

            val frame = EditorDialogFrame(GOrderEditorForm(gorder))
            frame.onSaveItem = {
                val creating: Boolean = gorder.id == null
                gorder.save()
                val op: String = if (creating) "добавлен" else "сохранен"
                val n = Notification.show("Заказ успешно ${op}.", 3000, Notification.Position.TOP_END)
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                onGOrderChanged(gorder)
                frame.closeDialog()
            }
            frame.onDeleteItem = { item -> maybeDelete(frame, item) }
            frame.open(gorder, gorder.id == null)
        }
    }
}