package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.db
import com.github.vokorm.findAllBy
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import com.iv.piter.convertToDateViaInstant
import com.iv.piter.convertToLocalDate
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Table("shedule")
data class Shedule(
    override var id: Long? = null,
    var guide_id: Long? = null,
    var day: LocalDate? = null,
    var start: LocalTime? = null,
    var finish: LocalTime? = null
) : KEntity<Long> {
    companion object : Dao<Shedule, Long>(Shedule::class.java) {
        //        fun  findByGuideId(guideId: Long, date: LocalDate): List<Shedule> {
//           return Shedule.findAllBy { Shedule::guide_id eq guideId}.filter { it.start?.let { it1 ->
//                convertToDateViaInstant(it1)}?.let { it2 -> convertToLocalDate(it2) } == date }
//        }
        fun findByGuideId(guideId: Long, date: LocalDate): List<Shedule> =
            Shedule.findAllBy { (Shedule::guide_id eq guideId).and(Shedule::day eq date) }
    }

    override fun delete() {
        db {
            super.delete()
        }
    }

}
