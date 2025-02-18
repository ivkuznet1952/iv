package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.db
import com.github.vokorm.findAllBy
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Table("gorder")
data class GOrder(override var id: Long? = null,
                  var num: Int = 0,
                  var trip_id: Long = 0,
                  var count: Int = 0,
                  var day: LocalDate? = null,
                  var start: LocalTime? = null,
                  var transport_id: Long = 0,
                  var guide_id: Long = 0,
                  var cost: Int = 0,
                  var status: OrderStatus? = null,
                  var paystatus: PayOrderStatus? = null,
                  var is_online: Boolean = false,
                  var customer_id: Long = 0,
                  var comment: String? = "",
                  var created: LocalDateTime? = null,
                  var updated: LocalDateTime? = null,
                  var createdby_id: Long? = null,
                  var archived: Boolean? = false
                  ) : KEntity<Long> {

    companion object : Dao<GOrder, Long>(GOrder::class.java) {

        fun findByGuideAndDay(guideId: Long, date: LocalDate): List<GOrder> =
            GOrder.findAllBy { (GOrder::guide_id eq guideId).and(GOrder::day eq date)
                .and(GOrder::archived eq false) }
                .sortedWith(compareBy { it.id })

        //fun isGOrdersExist(guideId: Long, date: LocalDate): Boolean = findByGuideAndDay(guideId, date).isNotEmpty()

        fun findByGuideAndMonth(guideId: Long, mm: Int, yyyy: Int): List<GOrder> =
            GOrder.findAllBy { (GOrder::guide_id eq guideId).and(GOrder::archived eq false) }
                .filter { it.day?.year == yyyy && it.day?.month?.value == mm }

    }

    override fun delete() {
        db {
            super.delete()
        }
    }

}

/**
 * Holds the join of GOrder and its others.
 */
/*
data class GOrderDTO(
    @field:Nested
    var gOrder: GOrder? = null,
    var tur_name: String? = null,
    var group_date: LocalDate? = null,
    var group_num: Int? = null,
    var customerFirstName: String? = null,
    var customerLastName: String? = null,
    var customerPhone: String? = null,
    var agency_name: String? = null,

    ) : Serializable {
    companion object {

        const val query = """
                select o.*,
                COALESCE(t.name, '') as tur_name,
                g.begin as group_date,
                g.num as group_num,
                COALESCE(c.firstname, '') as customerFirstName,
                COALESCE(c.lastname, '') as customerLastName,
                COALESCE(c.phone, '') as customerPhone,
                COALESCE(a.name, '') as agency_name
                FROM gorder o
                left join tur t on o.tur_id = t.id
                left join ggroup g on o.group_id = g.id
                left join customer c on o.customer_type = 0 and o.customer_id = c.id
                left join agency a on o.customer_type = 1 and o.customer_id = a.id
                """
        val dataProvider: EntityDataProvider<GOrderDTO>
    get() = EntityDataProvider(
        DaoOfJoin(GOrderDTO::class.java, query)
    )
    }
} */
//ORDER BY o.id asc
/*
fun EntityDataProvider<GOrderDTO>.setFilterTextGOrderDTO(filter: String?) {

   // val org_id = MainLayout.currentOrgId()
    if (filter.isNullOrBlank()) {
//        this.filter = buildCondition<GOrderDTO> {
//            """o.org_id=$org_id order by o.id desc""".trimMargin()()
//        }
    } else {
        val normalizedFilter: String = "%" + filter.trim().lowercase() + "%"
        val cond: Condition = buildCondition<GOrderDTO> {
        """(t.name LIKE :filter
                    or LOWER(c.firstname) LIKE :filter
                    or LOWER(c.lastname) LIKE :filter
                    or LOWER(a.name) LIKE :filter
                    or LOWER(t.name) LIKE :filter
                    or LOWER(o.status) LIKE :filter
                    or LOWER(o.paystatus) LIKE :filter
                    or CAST(o.cost as varchar(10)) LIKE :filter
                    or CAST(o.num as varchar(10)) LIKE :filter
                    or TO_CHAR(g.begin, 'DD.MM.YYYY') LIKE :filter
                    or COALESCE(LOWER(t.name), '') LIKE :filter
                    or COALESCE(LOWER(c.firstname), '') LIKE :filter
                    or COALESCE(LOWER(c.lastname), '') LIKE :filter
                    or COALESCE(LOWER(c.phone), '') LIKE :filter) and o.org_id=$org_id order by o.id desc
                    """.trimMargin()("filter" to normalizedFilter)
        }
        this.filter = cond
    }
}
*/

enum class OrderStatus {
    НОВЫЙ,
    ПРИНЯТ,
    ЗАВЕРШЕН,
    ОТМЕНЕН
}
enum class PayOrderStatus {
    НЕОПЛАЧЕН,
    ЧАСТИЧНО,
    ОПЛАЧЕН,
    ВЗАИМОЗАЧЕТ
}


