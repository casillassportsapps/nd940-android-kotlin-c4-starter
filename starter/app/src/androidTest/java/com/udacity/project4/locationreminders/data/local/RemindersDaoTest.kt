package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
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
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)
    }

    @Test
    fun getReminders() = runBlockingTest {
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.size, `is` (3))
    }

    @Test
    fun getReminderById() = runBlockingTest {
        val result = database.reminderDao().getReminderById("FakeId1")
        assertThat(result?.title, `is`("Test Title1"))
        assertThat(result?.description, `is`("Test Description 1"))
        assertThat(result?.location, `is`("Test Location 1"))
        assertThat(result?.latitude, `is`(40.000))
        assertThat(result?.longitude, `is`(70.000))
    }

    @Test
    fun deleteReminders() = runBlockingTest {
        database.reminderDao().deleteAllReminders()
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.size, `is`(0))
    }
}