package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.buildCondition
import com.github.vokorm.db
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import com.gitlab.mvysny.jdbiorm.condition.Condition
import com.gitlab.mvysny.jdbiorm.vaadin.EntityDataProvider
import jakarta.validation.constraints.NotBlank
import java.time.LocalTime

@Table("trip")
data class Trip(
    override var id: Long? = null,
    @field: NotBlank
    var name: String = "",
    var description: String? = "",
    var duration: Float = 0f,
    var comment: String? = "",
    var start: LocalTime? = null,
    var finish: LocalTime? = null,
    var active: Boolean = true,
    var photo: String? = "",
) : KEntity<Long> {
    companion object : Dao<Trip, Long>(Trip::class.java) {
    }

    override fun delete() {
        db {
            super.delete()
        }
    }

}

fun EntityDataProvider<Trip>.setFilterText(filter: String?) {

    if (filter.isNullOrBlank()) {
        this.filter = Condition.NO_CONDITION
    } else {
        val normalizedFilter: String = "%" + filter.trim().lowercase() + "%"
        val c: Condition = buildCondition<Transport>{
            """LOWER(name) LIKE :filter
                    """.trimMargin()("filter" to normalizedFilter)
        }
        this.filter = c
    }
}
