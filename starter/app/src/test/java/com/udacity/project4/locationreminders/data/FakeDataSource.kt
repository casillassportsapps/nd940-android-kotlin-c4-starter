package com.udacity.project4.locationreminders.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    //    TODO: Create a fake data source to act as a double to the real data source
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun cleanUp() {
        database.close()
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(database.reminderDao().getReminders())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        database.reminderDao().saveReminder(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        database.reminderDao().getReminderById(id)?.let {
            return Result.Success(it)

        }
        return Result.Error("No reminder with id $id")
    }

    override suspend fun deleteAllReminders() {
        database.reminderDao().deleteAllReminders()
    }
}