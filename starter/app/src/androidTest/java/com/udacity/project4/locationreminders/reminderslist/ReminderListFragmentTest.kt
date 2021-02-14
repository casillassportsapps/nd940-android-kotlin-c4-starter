package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.AndroidMainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationViewModel
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @get: Rule
    val mainCoroutineRule = AndroidMainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        appContext = getApplicationContext()
        stopKoin()

        val myModule = module {
            viewModel {
                RemindersListViewModel(appContext,
                get() as ReminderDataSource)
            }

            viewModel {
                AuthenticationViewModel()
            }

            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }

        startKoin {
            modules(listOf(myModule))
        }

        repository = get()

        runBlocking {
            repository.deleteAllReminders()
        }

        addData()
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
    fun showListData() {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))

        onView(withText("Test Title1")).check(matches(isDisplayed()))
        onView(withText("Test Title2")).check(matches(isDisplayed()))
        onView(withText("Test Title3")).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToSaveReminderFragment() {
        val fragmentScenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val controller = mock(NavController::class.java)
        fragmentScenario.onFragment {
            Navigation.setViewNavController(it.requireView(), controller)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(controller).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
    fun noDataShown() {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.noDataTextView)).check(matches(withText(appContext.getString(R.string.no_data))))
    }
}