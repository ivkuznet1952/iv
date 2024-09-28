package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.buildCondition
import com.github.vokorm.db
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import java.time.LocalDateTime

@Table("ordernumber")
data class OrderNumber(
                  override var id: Long? = null,
                  var num: Int = 0
) : KEntity<Long> {

    companion object : Dao<OrderNumber, Long>(OrderNumber::class.java) {
         //fun findAllByOrgId(org_id: Long): List<GOrderNumber> = findAllBy(buildCondition<GOrderNumber> { GOrderNumber::org_id eq org_id })
    }

    override fun delete() {
        db {
            super.delete()
        }
    }

}

