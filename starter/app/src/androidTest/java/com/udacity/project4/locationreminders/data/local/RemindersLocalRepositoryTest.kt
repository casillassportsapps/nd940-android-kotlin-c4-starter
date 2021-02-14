package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
        addData()
    }

    @After
    fun cleanUp() {
        database.close()
    }


    private fun addData() = runBlocking {
        val reminder1 = ReminderDTO("Test Title1", "Test Description 1", "Test Location 1", 40.000, 70.000, "FakeId1")
        val reminder2 = ReminderDTO("Test Title2", "Test Description 2", "Test Location 2", 41.000, 71.000, "FakeId2")
        val reminder3 = ReminderDTO("Test Title3", "Test Description 3", "Test Location 3", 42.000, 72.000, "FakeId3")
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        repository.saveReminder(reminder3)
    }

    @Test
    fun getReminders() = runBlockingTest {
        val result = repository.getReminders()
        if (result is Result.Success) {
            val size = result.data.size
            assertThat(size, `is` (3))
        }
    }

    @Test
    fun getReminderById() = runBlockingTest {
        val result = repository.getReminder("FakeId1")
        if (result is Result.Success<ReminderDTO>) {
            val reminder = result.data
            assertThat(reminder.title, `is`("Test Title1"))
            assertThat(reminder.description, `is`("Test Description 1"))
            assertThat(reminder.location, `is`("Test Location 1"))
            assertThat(reminder.latitude, `is`(40.000))
            assertThat(reminder.longitude, `is`(70.000))
        }
    }

    @Test
    fun deleteReminders() = runBlockingTest {
        repository.deleteAllReminders()
        val result = repository.getReminders()
        if (result is Result.Success) {
            val size = result.data.size
            assertThat(size, `is` (0))
        }
    }

}