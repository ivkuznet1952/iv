package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.iv.piter.ConfirmationDialog
import com.iv.piter.EditorDialogFrame
import com.iv.piter.EditorForm
import com.iv.piter.entity.Guide
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder


/**
 * A form for editing [Guide] objects.
 */
class GuideEditorForm(val guide: Guide) : VerticalLayout(), EditorForm<Guide> {

    override val itemType: String get() = "гид"
    override val binder: Binder<Guide> = beanValidationBinder()
    override var isEdit: Boolean = true

    init {
        isPadding = false
        formLayout {
            responsiveSteps {
                "0"(1); "320px"(1); "480px"(3)
                "580px"(3)
            }
            textField("Имя") {
                bind(binder)
                    .trimmingConverter()
                    .bind(Guide::firstname)
            }
            textField("Фамилия") {
                bind(binder)
                    .trimmingConverter()
                    .bind(Guide::lastname)
            }
            textField("Телефон") {
                bind(binder)
                    .trimmingConverter()
                    .bind(Guide::phone)
            }
        }
        horizontalLayout(spacing = true) {
            setWidthFull()
            textArea("Комментарий") {
                setWidthFull()
                bind(binder)
                    .trimmingConverter()
                    .bind(Guide::comment)
            }
        }

    }


    /**
     * Opens dialogs for editing [Guide] objects.
     * @property onGuideChanged called when a user has been created/edited/deleted.
     */
    class GuideEditorDialog(private val onGuideChanged: (Guide) -> Unit) { // TODO

        fun maybeDelete(frame: EditorDialogFrame<Guide>?, item: Guide) {

            val orderCount = 1L

            if (orderCount == 0L) {
                // delete(frame, item)
            } else {
                ConfirmationDialog().open(
                    "Удалить данные гида “${item.firstname}”  “${item.lastname}” ?",
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

        private fun delete(frame: EditorDialogFrame<Guide>, item: Guide) {
            item.delete()
            Notification.show("Данные успешно удалены.", 3000, Notification.Position.BOTTOM_START)
            frame.close()
            onGuideChanged(item)
        }

        fun createNew() {
            edit(Guide())
        }

        fun edit(guide: Guide) {

            val frame = EditorDialogFrame(GuideEditorForm(guide))
            frame.onSaveItem = {
                val creating: Boolean = guide.id == null
                guide.save()
                val op: String = if (creating) "добавлены" else "сохранены"
                val n = Notification.show("Данные гида успешно ${op}.", 3000, Notification.Position.TOP_END)
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                onGuideChanged(guide)
                frame.closeDialog()
            }
            frame.onDeleteItem = { item -> maybeDelete(frame, item) }
            frame.open(guide, guide.id == null)
        }
    }

}
