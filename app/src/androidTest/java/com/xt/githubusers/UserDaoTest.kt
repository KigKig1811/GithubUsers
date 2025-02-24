package com.xt.githubusers

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.xt.githubusers.data.room.dao.UserDao
import com.xt.githubusers.data.room.db.AppRoomDB
import com.xt.githubusers.data.room.entity.UserEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class UserDaoTest {

    private lateinit var database: AppRoomDB
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        // Create an in-memory database for testing (data will not persist)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppRoomDB::class.java
        ).allowMainThreadQueries().build()

        userDao = database.userDao()
    }

    @Test
    fun insertAll_and_FetchUsers_returnCorrectData() = runTest {
        // Given: A list of sample users
        val users = listOf(
            UserEntity(1, "John Doe"),
            UserEntity(2, "Jane Doe")
        )
        // When: Insert users into the database
        userDao.insertAll(users)

        // Then: Verify the data is correctly stored in the database
        val pagingSource = userDao.fetchUsers()
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null, loadSize = 10, placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(users, result.data) // Ensure inserted data matches expected data
    }

    @Test
    fun clearUsers_deletesAllData() = runTest {
        // Given: A list of users added to the database
        val users = listOf(
            UserEntity(1, "John Doe"),
            UserEntity(2, "Jane Doe")
        )
        userDao.insertAll(users)

        // When: Call clearUsers() to delete all data
        userDao.clearUsers()

        // Then: Verify the database is empty
        val pagingSource = userDao.fetchUsers()
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null, loadSize = 10, placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertTrue(result.data.isEmpty()) // Ensure the database is empty
    }

    @After
    fun tearDown() {
        // Close the database after tests are completed
        database.close()
    }
}
