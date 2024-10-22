package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.buildCondition
import com.github.vokorm.db
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import com.gitlab.mvysny.jdbiorm.condition.Condition
import com.gitlab.mvysny.jdbiorm.vaadin.EntityDataProvider
import jakarta.validation.constraints.NotBlank

@Table("guide")
data class Guide(override var id: Long? = null,
                     @field: NotBlank
                     var firstname: String = "",
                     @field: NotBlank
                     var lastname: String = "",
                     //var email: String? = "",
                     var phone: String? = "",
                     var active: Boolean = true,
                     var comment: String? = "",
) : KEntity<Long> {
    companion object : Dao<Guide, Long>(Guide::class.java) {
    }

    override fun delete() {
        db {
            super.delete()
        }
    }

}

fun EntityDataProvider<Guide>.setFilterText(filter: String?) {

    if (filter.isNullOrBlank()) {
        this.filter = Condition.NO_CONDITION
    } else {
        val normalizedFilter: String = "%" + filter.trim().lowercase() + "%"
        val c: Condition = buildCondition<Guide>{
            """LOWER(firstname) LIKE :filter LIKE :filter or LOWER(lastname) LIKE :filter or phone LIKE :filter
                     """.trimMargin()("filter" to normalizedFilter)
        }
        this.filter = c
    }
}
//or COALESCE(firstname, 'Undefined') LIKE :filter
