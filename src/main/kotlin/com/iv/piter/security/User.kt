package com.iv.piter.security

import com.github.mvysny.vaadinsimplesecurity.HasPassword
import com.github.vokorm.KEntity
import com.github.vokorm.buildCondition
import com.github.vokorm.findSingleBy
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import com.gitlab.mvysny.jdbiorm.vaadin.EntityDataProvider
import java.time.LocalDateTime
import com.gitlab.mvysny.jdbiorm.condition.Condition

@Table("users")
data class User(
                override var id: Long? = null,
                var username: String = "",
                private var hashedPassword: String = "",
                var roles: String = "",
                var active: Boolean = true,
                var created: LocalDateTime = LocalDateTime.now(),
                var updated: LocalDateTime = LocalDateTime.now()
                ) : KEntity<Long>, HasPassword {
    companion object : Dao<User, Long>(User::class.java) {
        fun findByUsername(username: String): User? = findSingleBy { User::username eq username }
    }

    override fun getHashedPassword(): String = hashedPassword

    override fun setHashedPassword(hashedPassword: String) {
        this.hashedPassword = hashedPassword
    }

    val roleSet: Set<String> get() = roles.split(",").toSet()
}

fun EntityDataProvider<User>.setFilterText(filter: String?) {

    if (filter.isNullOrBlank()) {
        this.filter = buildCondition<User>{ Condition.NO_CONDITION }
    } else {
        val normalizedFilter: String = "%" + filter.trim().lowercase() + "%"
        this.filter = buildCondition<User> {
            """LOWER(username) LIKE :filter or LOWER(roles) LIKE :filter
                    or COALESCE(username, '') LIKE :filter or COALESCE(roles, '') LIKE :filter
                    """.trimMargin()("filter" to normalizedFilter)
        }
    }
}
