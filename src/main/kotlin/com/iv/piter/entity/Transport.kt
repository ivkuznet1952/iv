package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.buildCondition
import com.github.vokorm.db
import com.github.vokorm.findSingleBy
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import com.gitlab.mvysny.jdbiorm.condition.Condition
import com.gitlab.mvysny.jdbiorm.vaadin.EntityDataProvider
import jakarta.validation.constraints.NotBlank

@Table("transport")
data class Transport(override var id: Long? = null,
                @field: NotBlank
                var name: String = ""
                ) : KEntity<Long> {
    companion object : Dao<Transport, Long>(Transport::class.java) {
       fun findByName(name: String): Transport? = findSingleBy { Transport::name eq name }
    }

    override fun delete() {
        db {
        //    if (id != null) {
        //        handle.createUpdate("update TOrder set transport_id = NULL where transport_id=:id")
        //            .bind("id", id!!)
        //            .execute()
        //    }
            super.delete()
        }
    }

}

fun EntityDataProvider<Transport>.setFilterText(filter: String?) {

    if (filter.isNullOrBlank()) {
        this.filter = Condition.NO_CONDITION
    } else {
        val normalizedFilter: String = "%" + filter.trim().lowercase() + "%"
        val c: Condition = buildCondition<Transport>{
            """name LIKE :filter
                    or COALESCE(name, 'Undefined') LIKE :filter
                    """.trimMargin()("filter" to normalizedFilter)
        }
        this.filter = c
    }
}
