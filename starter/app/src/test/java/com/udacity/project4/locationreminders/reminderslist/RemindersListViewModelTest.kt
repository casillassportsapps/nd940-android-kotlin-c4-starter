package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setUp() {
        dataSource = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
        addData()
    }

    private fun addData() = runBlocking {
        val reminder1 = ReminderDTO("Test Title1", "Test Description 1", "Test Location 1", 40.000, 70.000)
        val reminder2 = ReminderDTO("Test Title2", "Test Description 2", "Test Location 2", 41.000, 71.000)
        val reminder3 = ReminderDTO("Test Title3", "Test Description 3", "Test Location 3", 42.000, 72.000)
        dataSource.saveReminder(reminder1)
        dataSource.saveReminder(reminder2)
        dataSource.saveReminder(reminder3)
    }

    @After
    fun clear()  = runBlocking {
        dataSource.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun loadReminders() = runBlocking {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))

        val reminders = viewModel.remindersList.getOrAwaitValue()
        assertThat(reminders.size, `is` (3))
    }

    @Test
    fun clearReminders_ShowNoData() = runBlocking {
        dataSource.deleteAllReminders()
        viewModel.loadReminders()

        val reminders = viewModel.remindersList.getOrAwaitValue()
        assertThat(reminders.size, `is` (0))
        assertThat(viewModel.showNoData.value == true, `is`(true))
    }
}