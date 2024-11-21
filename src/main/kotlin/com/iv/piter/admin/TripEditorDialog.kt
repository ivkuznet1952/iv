package com.iv.piter.admin

import com.github.mvysny.karibudsl.v10.*
import com.iv.piter.ConfirmationDialog
import com.iv.piter.EditorDialogFrame
import com.iv.piter.EditorForm
import com.iv.piter.UploadRussianI18N
import com.iv.piter.entity.Trip
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.converter.StringToFloatConverter
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*


class TripEditorForm(private var trip: Trip) : FormLayout(), EditorForm<Trip> {
    override val itemType: String get() = "экскурсию"
    override val binder: Binder<Trip> = beanValidationBinder()
    override var isEdit:Boolean = true

    init {
        maxWidth = "520px"
        responsiveSteps {
            "0"(1); "320px"(1); "480px"(1)
            "580px"(1)
        }
        textArea("Наименование") {
            bind(binder)
                .trimmingConverter()
                .asRequired()
                .bind(Trip::name)
        }
        horizontalLayout(padding = false, spacing = true) {
            textField("Продолжительность(час.)") {
                width = "25%"
                bind(binder).withConverter<Float>(
                    StringToFloatConverter("Требуется число")
                ).bind(Trip::duration)
            }

            timePicker("Начало") {
                width = "30%"
                locale = Locale.getDefault()
                bind(binder).bind(Trip::start)
            }
            timePicker("Окончание") {
                width = "30%"
                locale = Locale.getDefault()
                bind(binder).bind(Trip::finish)
            }
        }

        p{}
        horizontalLayout(padding = false, spacing = true) {

            val buffer = MemoryBuffer()
            val upload = Upload(buffer)
            nativeLabel("Фото") {
                style.set("font-size", "14px")
                style.set("color", "lightgray")
                upload
            }
            upload.isAutoUpload = true
            upload.setWidthFull()
            val i18n = UploadRussianI18N ()
            i18n.getAddFiles().setMany("Выберите файл...")
            upload.i18n = i18n
            add(upload)
            upload.addSucceededListener { event ->
                val fileData = buffer.inputStream
                val fileName = event.getFileName()
                val bytes = fileData.readBytes()
                trip.photo = bytes
                val stream: InputStream = ByteArrayInputStream(bytes)
                val imageResource = StreamResource(fileName, InputStreamFactory { stream })
                val image = Image(imageResource, "image")
                image.width = "50px"
                br {}
                add(image)
            }
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

                val orderCount = 0L

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
                   // println("//////:" + trip)
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

