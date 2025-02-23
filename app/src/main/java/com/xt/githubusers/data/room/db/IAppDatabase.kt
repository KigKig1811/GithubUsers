package com.xt.githubusers.data.room.db

import com.xt.githubusers.data.room.dao.RemoteKeyDao
import com.xt.githubusers.data.room.dao.UserDao

interface IAppDatabase {
    fun userDao(): UserDao
    fun remoteKeyDao(): RemoteKeyDao
}