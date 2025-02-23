package com.xt.githubusers.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xt.githubusers.data.room.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: RemoteKeyEntity)

    @Query("SELECT * FROM remote_key_table WHERE id = :id")
    suspend fun remoteKey(id: Int): RemoteKeyEntity

    @Query("DELETE FROM remote_key_table")
    suspend fun clearRemoteKeys()
}