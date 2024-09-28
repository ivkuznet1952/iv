package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.github.vokorm.db
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Table("customer")
data class Customer(override var id: Long? = null,
                    @field: NotBlank
                    var firstname: String = "",
                    @field: NotBlank
                    var lastname: String = "",
                    private var hashedPassword: String = "",
                    var email: String? = "",
                    var phone: String? = "",
                    var created: LocalDateTime? = null,
                    var updated: LocalDateTime? = null,
                    var comment: String? = "",
) : KEntity<Long> {
    companion object : Dao<Customer, Long>(Customer::class.java) {
    }

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
