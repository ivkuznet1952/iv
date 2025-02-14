package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.db
import com.github.vokorm.findAllBy
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import java.time.LocalDate
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
        fun findByGuideAndDay(guideId: Long, day: LocalDate): List<Shedule> =
            Shedule.findAllBy { (Shedule::guide_id eq guideId).and(Shedule::day eq day) }
                .sortedWith(compareBy { it.id })

        //fun isSheduleExists(guideId: Long, day: LocalDate): Boolean = findByGuideAndDay(guideId, day).isNotEmpty()

        fun findByGuideAndMonth(guideId: Long, mm: Int, yyyy: Int): List<Shedule> =
            Shedule.findAllBy { Shedule::guide_id eq guideId }
                .filter { it.day?.year == yyyy && it.day?.month?.value == mm }

    }


    override fun delete() {
        db {
            super.delete()
        }
    }

}
