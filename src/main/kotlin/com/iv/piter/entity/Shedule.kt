package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.db
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import java.time.LocalTime

@Table("shedule")
data class Shedule(
    override var id: Long? = null,
    var trip_id: Long? = null,
    var begin: LocalTime,
   // var active: Boolean = false
) : KEntity<Long> {
    companion object : Dao<Shedule, Long>(Shedule::class.java) {
    }

    override fun delete() {
        db {
//            super.delete()
        }
    }

}
