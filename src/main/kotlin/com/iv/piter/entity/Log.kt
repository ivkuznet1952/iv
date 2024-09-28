package com.iv.piter.entity

import com.github.vokorm.KEntity
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.Table
import java.time.LocalDateTime

@Table("log")
data class Log(
    override var id: Long? = null,
    var created: LocalDateTime = LocalDateTime.now(),
    var user_id: Long = 0L,
    var action: String = "",
    var comment: String = "",
) : KEntity<Long> {
    companion object : Dao<Log, Long>(Log::class.java) {
    }
}
