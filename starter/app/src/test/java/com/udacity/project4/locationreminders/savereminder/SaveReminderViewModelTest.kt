package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.nullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    //TODO: provide testing to the SaveReminderView and its live data objects

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel


    @Before
    fun setUp() {
        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @After
    fun clear() = runBlockingTest {
        dataSource.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun saveReminder_retrieveReminder() = runBlockingTest {
        val reminder = ReminderDataItem(
            "Test Title",
            "Test Description",
            "Test Location",
            40.000,
            71.000,
            "fakeID"
        )

        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(reminder)
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(viewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved!"))

        val result = dataSource.getReminder("fakeID")
        if (result is Result.Success<ReminderDTO>) {
            val reminderDTO = result.data
            assertThat(reminderDTO.title, `is`("Test Title"))
            assertThat(reminderDTO.description, `is`("Test Description"))
            assertThat(reminderDTO.location, `is`("Test Location"))
            assertThat(reminderDTO.latitude, `is`(40.000))
            assertThat(reminderDTO.longitude, `is`(71.000))
        }
    }

    @Test
    fun onClear() {
        viewModel.onClear()
        assertThat(viewModel.reminderTitle.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.reminderDescription.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.reminderSelectedLocationStr.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.selectedPOI.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.latitude.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.longitude.getOrAwaitValue(), `is`(nullValue()))
    }

    @Test
    fun validateTitle() {
        val reminder = ReminderDataItem(null, null, null, null, null)
        viewModel.validateEnteredData(reminder)
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title))
    }

    @Test
    fun validateLocation() {
        val reminder = ReminderDataItem("title", null, null, null, null)
        viewModel.validateEnteredData(reminder)
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location))
    }
}