package com.iv.piter

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.ModifierKey.*
import com.github.mvysny.kaributools.addClickShortcut
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.Key.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

/**
 * A toolbar with a search box and a "Create New" button. Don't forget to provide proper listeners
 * for [onSearch] and [onCreate].
 * @param createCaption the caption of the "Create New" button.
 */
class Toolbar(createCaption: String) : KComposite() {
    /**
     * Fired when the text in the search text field changes.
     */
    var onSearch: (String)->Unit = {}
    /**
     * Fired when the "Create new" button is clicked.
     */
    var onCreate: ()->Unit = {}
    lateinit var searchField: TextField
    /**
     * Current search text. Never null, trimmed, may be blank.
     */
    val searchText: String get() = searchField.value.trim()

    public var addButton: Button? = null

    private val root = ui {
        div("view-toolbar") {
            searchField = textField {
                prefixComponent = Icon(VaadinIcon.SEARCH)
                addClassNames("view-toolbar__search-field")
                placeholder = "Поиск"
                addValueChangeListener { onSearch(searchText) }
                valueChangeMode = ValueChangeMode.EAGER

            }
            addButton = button("$createCaption") {
                setPrimary()
                addClassName("view-toolbar__button")
                addClickListener { onCreate() }
            }
        }
    }
}

@VaadinDsl
fun (@VaadinDsl HasComponents).toolbarView(createCaption: String, block: (@VaadinDsl Toolbar).() -> Unit = {})
        = init(Toolbar(createCaption), block)

