package com.xt.githubusers.data.di

import android.content.Context
import android.os.Debug
import androidx.room.Room
import com.xt.githubusers.data.room.db.AppRoomDB
import com.xt.githubusers.data.room.db.IAppDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppRoomDB =
        Room.databaseBuilder(
            context = context,
            klass = AppRoomDB::class.java,
            name = "APP_DB"
        ).fallbackToDestructiveMigration().apply {
            if (Debug.isDebuggerConnected()) {
                allowMainThreadQueries()
            }
        }.build()
}

@Module
@InstallIn(SingletonComponent::class)
interface RoomBinds {

    @Binds
    @Singleton
    fun bindAppDatabase(db: AppRoomDB): IAppDatabase

}

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideUserDao(db: IAppDatabase) = db.userDao()

    @Provides
    @Singleton
    fun provideRemoteKeyDao(db: IAppDatabase) = db.remoteKeyDao()

}
