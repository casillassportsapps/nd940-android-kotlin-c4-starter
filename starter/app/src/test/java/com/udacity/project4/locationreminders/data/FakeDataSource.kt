package com.udacity.project4.locationreminders.data


import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
class FakeDataSource : ReminderDataSource {

    //    TODO: Create a fake data source to act as a double to the real data source
    private val data = mutableMapOf<String, ReminderDTO>()


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(data.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        data[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        data[id]?.let {
            return Result.Success(it)

        }
        return Result.Error("No reminder with id $id")
    }

    override suspend fun deleteAllReminders() {
        data.clear()
    }
}