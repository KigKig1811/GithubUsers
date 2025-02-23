package com.xt.githubusers.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xt.githubusers.data.room.entity.UserEntity.Companion.TABLE_USER

@Entity(tableName = TABLE_USER)
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int? = null,
    val login: String? = null,
    val nodeId: String? = null,
    val type: String? = null,
    val siteAdmin: Boolean? = null,
    val avatarUrl: String? = null,
    val htmlUrl: String? = null,
    val url: String? = null,
    val gravatarId: String? = null,
    val eventsUrl: String? = null,
    val followersUrl: String? = null,
    val followingUrl: String? = null,
    val gistsUrl: String? = null,
    val organizationsUrl: String? = null,
    val receivedEventsUrl: String? = null,
    val reposUrl: String? = null,
    val starredUrl: String? = null,
    val subscriptionsUrl: String? = null,
    val userViewType: String? = null
) {
    companion object {
        const val TABLE_USER = "user_table"
    }
}
