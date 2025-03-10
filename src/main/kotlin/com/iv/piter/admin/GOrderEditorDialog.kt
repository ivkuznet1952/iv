package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.iv.piter.EditorDialogFrame
import com.iv.piter.EditorForm
import com.iv.piter.entity.GOrder
import com.iv.piter.entity.GOrderDTO
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.binder.Setter
import com.vaadin.flow.data.validator.StringLengthValidator
import com.vaadin.flow.function.ValueProvider

class GOrderEditorForm(val gorderDTO: GOrderDTO) : FormLayout(), EditorForm<GOrder> {

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
                        "Комментарий",
                        0, null
                    )
                ) .bind(GOrder::comment)



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
    class GOrderEditorDialog(private val onGOrderChanged: (GOrderDTO) -> Unit) {
        private fun maybeDelete(frame: EditorDialogFrame<GOrderDTO>, item: GOrderDTO) { // TODO

        }

        private fun delete(frame: EditorDialogFrame<GOrderDTO>, item: GOrderDTO) {
            item.gOrder?.delete()
            Notification.show("Заказ успешно удален.", 3000, Notification.Position.BOTTOM_START)
            frame.close()
            onGOrderChanged(item)
        }

        fun createNew() {
            val gOrderDTO = GOrderDTO()
            gOrderDTO.gOrder = GOrder()
            edit(gOrderDTO)
        }

        fun edit(gorderDTO: GOrderDTO) {

            val frame = EditorDialogFrame(GOrderEditorForm(gorderDTO))
            frame.deleteButton.text = "В архив"
             frame.onSaveItem = {
                val creating: Boolean = gorderDTO.gOrder?.id == null
                gorderDTO.gOrder?.save()
                val op: String = if (creating) "добавлен" else "сохранен"
                val n = Notification.show("Заказ успешно ${op}.", 3000, Notification.Position.TOP_END)
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                onGOrderChanged(gorderDTO)
                frame.closeDialog()
            }
            //frame.onDeleteItem = { item -> maybeDelete(frame, item) }
            gorderDTO.gOrder?.let { frame.open(it, gorderDTO.gOrder?.id == null) }
        }
    }
}