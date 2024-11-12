package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.db
import com.github.vokorm.findAllBy
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table

@Table("cost")
data class Cost(
    override var id: Long? = null,
    var trip_id: Long? = null,
    var transport_id: Long? = null,
    var cost: Int = 0,
   // var active: Boolean = false
) : KEntity<Long> {
    companion object : Dao<Cost, Long>(Cost::class.java) {
       fun  findByTripId(tripId: Long): List<Cost> = Cost.findAllBy { Cost::trip_id eq tripId }
    }

    override fun delete() {
        db {
            super.delete()
        }
    }

}
