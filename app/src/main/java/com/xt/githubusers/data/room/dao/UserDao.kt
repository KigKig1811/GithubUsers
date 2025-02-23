package com.xt.githubusers.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xt.githubusers.data.room.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<UserEntity>)

    @Query("SELECT * FROM USER_TABLE ORDER BY id ASC")
    fun fetchUsers(): PagingSource<Int, UserEntity>

    @Query("DELETE FROM USER_TABLE")
    suspend fun clearUsers()
}