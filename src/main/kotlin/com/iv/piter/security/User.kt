package com.iv.piter.security

import com.github.mvysny.vaadinsimplesecurity.HasPassword
import com.github.vokorm.KEntity
import com.github.vokorm.findSingleBy
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import java.time.LocalDateTime

@Table("users")
data class User(override var id: Long? = null,
                var username: String = "",
                private var hashedPassword: String = "",
                var roles: String = "",
                var created: LocalDateTime = LocalDateTime.now(),
                var updated: LocalDateTime = LocalDateTime.now()
                ) : KEntity<Long>, HasPassword {
    companion object : Dao<User, Long>(User::class.java) {
        /**
         * Finds user by his [username]. If there is no such user, returns `null`.
         */
        fun findByUsername(username: String): User? = findSingleBy { User::username eq username }
    }

    override fun getHashedPassword(): String = hashedPassword

    override fun setHashedPassword(hashedPassword: String) {
        this.hashedPassword = hashedPassword
    }

    val roleSet: Set<String> get() = roles.split(",").toSet()
}
