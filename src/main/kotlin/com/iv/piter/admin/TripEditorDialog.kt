package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.iv.piter.ConfirmationDialog
import com.iv.piter.EditorDialogFrame
import com.iv.piter.EditorForm
import com.iv.piter.UploadRussianI18N
import com.iv.piter.entity.Trip
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.converter.StringToFloatConverter
import com.vaadin.flow.data.converter.StringToIntegerConverter
import java.io.File


class TripEditorForm(val trip: Trip) : FormLayout(), EditorForm<Trip> {
    private val isEditing get() = trip.id != null
    override val itemType: String get() = "экскурсию"
    override val binder: Binder<Trip> = beanValidationBinder()
    override var isEdit:Boolean = true
    var photoField: TextField = TextField()

    init {
        maxWidth = "500px"
        responsiveSteps {
            "0"(1); "320px"(1); "480px"(1)
            "580px"(1)
        }
        textField("Наименование") {
            bind(binder)
                .trimmingConverter()
                .asRequired()
                .bind(Trip::name)
        }

        textField ("Продолжительность экскурсии (час.)"){
            bind(binder).withConverter<Float>(
                StringToFloatConverter("Not a number")
            ).bind(Trip::duration)
        }



        horizontalLayout(padding = false, spacing = false) {
            photoField = textField("Фото") {
                setWidthFull()
                bind(binder).bind(Trip::photo)
            }
        }
       // upload image
        val buffer = MemoryBuffer()
        val upload = Upload(buffer)
        upload.isAutoUpload = true

        val i18n = UploadRussianI18N ()
        i18n.getAddFiles().setMany("Выберите файл...")
        upload.i18n = i18n
        add(upload)
        val img_path = "src/main/resources/webapp/images/" // TODO
        upload.addSucceededListener { event ->
            val fileData = buffer.inputStream
            val fileName = event.getFileName()
            //event.contentLength
            val file = File(img_path + fileName)
            file.createNewFile()
            photoField.value = fileName
            val bytes = fileData.readBytes()
            file.writeBytes(bytes)
        }

        textArea("Описание") {
            bind(binder)
                .trimmingConverter()
                .asRequired()
                .bind(Trip::description)
        }

        textArea("Комментарий") {
            bind(binder)
                .trimmingConverter()
                .asRequired()
                .bind(Trip::comment)
        }
    }


    /**
     * Opens dialogs for editing [Trip] objects.
     * @property onTripChanged called when a trip has been created/edited/deleted.
     */
    class TripEditorDialog(private val onTripChanged: (Trip) -> Unit) {

        fun maybeDelete(frame: EditorDialogFrame<Trip>?, item: Trip) { // TODO

            val orderCount = 1L

            //println("////////////:" + frame)

            if (orderCount == 0L) {
                if (frame != null) {
                    delete(frame, item)
                }
            } else {
                ConfirmationDialog().open(
                    "Удалить данные по экскурсии “${item.name}”?",
                    "Число заказов, ассоциированных с данной экскурсией, больше 0.",
                    "Удаление данной экскурсии приведет к наличию неопределенных данных.",
                    "Удалить",
                    true,
                    false,
                    false
                ) {
                    if (frame != null) delete(frame, item)
                }
            }
        }

        private fun delete(frame: EditorDialogFrame<Trip>, item: Trip) {
            //item.id?.let { Trip.deleteById(it) }
            item.delete()
            val n = Notification.show("Экскурсия успешно удалена.", 3000, Notification.Position.TOP_END)
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
            frame.close()
            onTripChanged(item)
        }

        fun createNew() {
            edit(Trip())
        }

        fun edit(trip: Trip) {

            val frame = EditorDialogFrame(TripEditorForm(trip))

            frame.onSaveItem = {
                val creating: Boolean = trip.id == null
                if (trip.photo != null) trip.photo = trip.photo!!.lowercase()
                trip.save()
                val op: String = if (creating) "добавлена" else "сохранена"
                val n = Notification.show("Экскурсия успешно ${op}.", 3000, Notification.Position.TOP_END)
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                onTripChanged(trip)
                frame.closeDialog()
            }
            frame.onDeleteItem = { item -> maybeDelete(frame, item) }
            frame.open(trip, trip.id == null)
        }
    }

}

