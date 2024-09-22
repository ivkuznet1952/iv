package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.db
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import jakarta.validation.constraints.NotBlank

@Table("trip")
data class Trip(
    override var id: Long? = null,
    @field: NotBlank
    var name: String = "",
    var description: String = "",
    var photo: String = "",
    var duration: Int = 0,
    var comment: String? = "",
    var active: Boolean? = true,
) : KEntity<Long> {
    companion object : Dao<Trip, Long>(Trip::class.java) {
    }

    override fun delete() {
        db {
//            super.delete()
        }
    }

}
