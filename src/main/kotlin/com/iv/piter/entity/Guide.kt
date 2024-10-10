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
                     var email: String? = "",
                     var phone: String? = "",
                     var active: Boolean = true,
                     var comment: String? = "",
) : KEntity<Long> {
    companion object : Dao<Guide, Long>(Guide::class.java) {
    }

    override fun delete() {
        db {
        //    if (id != null) {
        //        handle.createUpdate("update TOrder set guide_id = NULL where guide_id=:id")
        //            .bind("id", id!!)
        //            .execute()
        //    }
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
            """firstname LIKE :filter
                    or COALESCE(firstname, 'Undefined') LIKE :filter
                    or COALESCE(lastname, 'Undefined') LIKE :filter
                    or COALESCE(phone, 'Undefined') LIKE :filter
                    or COALESCE(email, 'Undefined') LIKE :filter
                    """.trimMargin()("filter" to normalizedFilter)
        }
        this.filter = c
    }
}