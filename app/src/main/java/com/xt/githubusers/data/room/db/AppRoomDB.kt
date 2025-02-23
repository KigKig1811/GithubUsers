package com.xt.githubusers.data.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xt.githubusers.data.room.entity.RemoteKeyEntity
import com.xt.githubusers.data.room.entity.UserEntity

@Database(entities = [UserEntity::class, RemoteKeyEntity::class], version = 1, exportSchema = true)
abstract class AppRoomDB : RoomDatabase(), IAppDatabase
