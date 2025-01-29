package com.iv.piter.entity

import com.github.mvysny.vaadinsimplesecurity.HasPassword
import com.github.vokorm.*
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import com.gitlab.mvysny.jdbiorm.condition.Condition
import com.gitlab.mvysny.jdbiorm.vaadin.EntityDataProvider
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Table("customer")
data class Customer(override var id: Long? = null,
                    @field: NotBlank
                    var username: String = "",
                    @field: NotBlank
                    var firstname: String = "",
                    @field: NotBlank
                    var lastname: String = "",
                    var chatid: String = "",
                    var email: String? = "",
                    var phone: String? = "",
                    var created: LocalDateTime? = null,
                    var updated: LocalDateTime? = null,
                    var comment: String? = "",
                    var active: Boolean = true
) : KEntity<Long>  {
    companion object : Dao<Customer, Long>(Customer::class.java) {
        fun findByCustomername(username: String): Customer? = findSingleBy { Customer::username eq username }
        fun existsWithName(username: String): Boolean = existsBy{ Customer::username eq username }

    }
//    override fun getHashedPassword(): String = hashedPassword
//
//    override fun setHashedPassword(hashedPassword: String) {
//        this.hashedPassword = hashedPassword
//    }

    override fun delete() {
        db {
//            if (id != null) {
//                handle.createUpdate("update TOrder set customer_id = NULL where customer_id=:id")
//                    .bind("id", id!!)
//                    .execute()
//            }
            super.delete()
        }
    }

}

fun EntityDataProvider<Customer>.setFilterText(filter: String?) {

    if (filter.isNullOrBlank()) {
        this.filter = buildCondition<Customer>{ Condition.NO_CONDITION }
    } else {
        val normalizedFilter: String = "%" + filter.trim().lowercase() + "%"
        this.filter = buildCondition<Customer> {
            """LOWER(firstname) LIKE :filter or LOWER(lastname) LIKE :filter or LOWER(phone) LIKE :filter
                or LOWER(username) LIKE :filter 
                    """.trimMargin()("filter" to normalizedFilter)
        }
    }
}
