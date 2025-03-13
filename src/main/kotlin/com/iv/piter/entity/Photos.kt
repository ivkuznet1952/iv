package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.db
import com.github.vokorm.findAllBy
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table

@Table("photos")
data class Photos(
    override var id: Long? = null,
    var name: String? = null,
    var trip_id: Long? = null,
    var bytes: ByteArray? = ByteArray(0),
) : KEntity<Long> {
    companion object : Dao<Photos, Long>(Photos::class.java) {
        fun findByTripIdAndName(tripId: Long, name: String?): Photos? =
            Photos.findAllBy { (Photos::trip_id eq tripId).and(Photos::name eq name) }.firstOrNull()

        fun findByTripId(tripId: Long): Photos? = Photos.findAllBy { (Photos::trip_id eq tripId) }.firstOrNull()

    }

    override fun delete() {
        db {
            super.delete()
        }
    }

}