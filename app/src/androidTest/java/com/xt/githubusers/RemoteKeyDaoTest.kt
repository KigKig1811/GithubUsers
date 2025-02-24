package com.xt.githubusers

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.xt.githubusers.data.room.dao.RemoteKeyDao
import com.xt.githubusers.data.room.db.AppRoomDB
import com.xt.githubusers.data.room.db.IAppDatabase
import com.xt.githubusers.data.room.entity.RemoteKeyEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemoteKeyDaoTest {

    private lateinit var database: AppRoomDB
    private lateinit var remoteKeyDao: RemoteKeyDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppRoomDB::class.java).build()

        remoteKeyDao = database.remoteKeyDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndRetrieveRemoteKey() = runTest {
        // Given: A RemoteKeyEntity with an ID of 1 and nextKey of 2
        val key = RemoteKeyEntity(id = 1, nextKey = 2, createdAt = System.currentTimeMillis())

        // When: Insert the entity into the database
        remoteKeyDao.insert(key)

        // Then: Retrieve the entity and verify it matches the expected values
        val result = remoteKeyDao.remoteKey(1)
        assertNotNull(result) // Ensure the retrieved entity is not null
        assertEquals(1, result.id) // Verify the ID
        assertEquals(2, result.nextKey) // Verify the nextKey value
    }

    @Test
    fun clearRemoteKeys() = runTest {
        // Given: Insert multiple RemoteKeyEntity records
        val key1 = RemoteKeyEntity(id = 1, nextKey = 2, createdAt = System.currentTimeMillis())
        val key2 = RemoteKeyEntity(id = 2, nextKey = 3, createdAt = System.currentTimeMillis())

        remoteKeyDao.insert(key1)
        remoteKeyDao.insert(key2)

        // When: Clear all remote keys from the database
        remoteKeyDao.clearRemoteKeys()

        // Then: Verify that no data exists in the database
        val result = remoteKeyDao.remoteKey(1)
        assertNull(result) // Ensure the retrieved result is null after deletion
    }

    @Test
    fun getCreationTime() = runTest {
        // Given: Insert multiple RemoteKeyEntity records with different creation timestamps
        val key1 = RemoteKeyEntity(id = 1, nextKey = 2, createdAt = 1700000000000L)
        val key2 = RemoteKeyEntity(id = 2, nextKey = 3, createdAt = 1800000000000L)

        remoteKeyDao.insert(key1)
        remoteKeyDao.insert(key2)

        // When: Retrieve the latest creation time from the database
        val creationTime = remoteKeyDao.getCreationTime()

        // Then: Verify that the retrieved creation time is the latest one
        assertEquals(1800000000000L, creationTime)
    }

}