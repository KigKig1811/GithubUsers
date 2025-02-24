package com.xt.githubusers.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_key_table")
data class RemoteKeyEntity(
    @PrimaryKey
    val id: Int,
    val nextKey: Int?,
    val createdAt: Long = System.currentTimeMillis()
)
