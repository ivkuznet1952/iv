package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.buildCondition
import com.github.vokorm.db
import com.github.vokorm.findAllBy
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.DaoOfJoin
import com.gitlab.mvysny.jdbiorm.Table
import com.gitlab.mvysny.jdbiorm.condition.Condition
import com.gitlab.mvysny.jdbiorm.vaadin.EntityDataProvider
import org.jdbi.v3.core.mapper.Nested
import java.io.Serializable
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

        fun findByActual(actual: Boolean): List<GOrder> {
            var date: LocalDate = LocalDate.now()
            if (!actual) date = LocalDate.of(1970, 1, 1)
            return GOrder.findAllBy { (GOrder::archived eq false).and(GOrder::day ge date) }
        }

        fun findByArchved(): List<GOrder> = GOrder.findAllBy { (GOrder::archived eq true) }

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

data class GOrderDTO(
    @field:Nested
    var gOrder: GOrder? = null,
    var trip_name: String? = null,
    var customerFirstName: String? = null,
    var customerLastName: String? = null,
    var customerPhone: String? = null
    ) : Serializable {
    companion object {

        const val query = """
                select o.*,
                COALESCE(t.name, '') as trip_name,
                COALESCE(c.firstname, '') as customerFirstName,
                COALESCE(c.lastname, '') as customerLastName,
                COALESCE(c.phone, '') as customerPhone
                FROM gorder o
                left join trip t on o.trip_id = t.id
                left join customer c on o.customer_id = c.id
                """
        val dataProvider: EntityDataProvider<GOrderDTO>
    get() = EntityDataProvider(
        DaoOfJoin(GOrderDTO::class.java, query)
    )

    }
}


fun EntityDataProvider<GOrderDTO>.setFilterTextGOrderDTO(filter: String?, actual: Boolean) {

    var date: LocalDate = LocalDate.now()
    if (!actual) date = LocalDate.of(1970, 1, 1)
    if (filter.isNullOrBlank()) {
        this.filter = buildCondition<GOrderDTO> {
            """o.day>='$date'::date order by o.num desc""".trimMargin()()
        }
    } else {
        val normalizedFilter: String = "%" + filter.trim().lowercase() + "%"
        val cond: Condition = buildCondition<GOrderDTO> {
        """(t.name LIKE :filter
                    or LOWER(c.firstname) LIKE :filter
                    or LOWER(c.lastname) LIKE :filter
                     or LOWER(c.phone) LIKE :filter
                    or LOWER(t.name) LIKE :filter
                    or LOWER(o.status) LIKE :filter
                    or LOWER(o.paystatus) LIKE :filter
                    or CAST(o.cost as varchar(10)) LIKE :filter
                    or CAST(o.num as varchar(10)) LIKE :filter
                    or COALESCE(LOWER(t.name), '') LIKE :filter
                    or COALESCE(LOWER(c.firstname), '') LIKE :filter
                    or COALESCE(LOWER(c.lastname), '') LIKE :filter
                    or COALESCE(LOWER(c.phone), '') LIKE :filter) and o.day>='$date'::date order by o.num desc
                    """.trimMargin()("filter" to normalizedFilter)
        }
        this.filter = cond
    }
}

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


