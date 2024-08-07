package com.example.dosediary.viewmodel

import com.example.dosediary.model.entity.Medication
import java.util.Date
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.icu.util.TimeUnit
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.dosediary.MainActivity
import com.example.dosediary.state.ReminderState
import com.example.dosediary.state.UserState
import com.example.dosediary.utils.DoseDiaryDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val userState: UserState,
    @ApplicationContext private val context: Context
): ViewModel(){
    private val _userDao = DoseDiaryDatabase.getInstance(context).userDao
    private val _medDao = DoseDiaryDatabase.getInstance(context).medicationDao
    private val _alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val _reminderState = MutableStateFlow<ReminderState>(ReminderState.Idle)

    val reminderState: StateFlow<ReminderState> get() = _reminderState

    fun scheduleMedReminders(medName: String, times: List<Date>, startDate: Date, endDate: Date, note:String){
        // schedule medication reminders and send to Alarm Manager API
        viewModelScope.launch{
            //val medication = _medDao.getMedicationByID(medID).firstOrNull()
            try {
                times?.forEach{scheduleDate->
                    val nextReminderTimes = _calculateNextReminder(scheduleDate, startDate, endDate)
                    nextReminderTimes.forEach{nextReminderTime->
                        val intent = Intent(context, ReminderReceiver::class.java).apply {
                            putExtra("Name", medName)
                            putExtra("Message", "Medication Reminder")
                            putExtra("Note", note)
                        }
                        // generate a unique ID for intent
                        val requestCode = _generateUniqueRequestCode()
                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            requestCode,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        // schedule in alarm manager
                        _alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            nextReminderTime,
                            pendingIntent
                        )

                        Log.d("ReminderViewModel", "Scheduled reminder for: $medName at $nextReminderTime")

                    }
                }
                // on success
                _reminderState.value = ReminderState.Success("Reminders set successfully")
            }catch(e:Exception){
                // on error
                _reminderState.value = ReminderState.Error(e.message ?: "Unknown Error")
            }

        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun scheduleRefill(medName: String, refillDays: Int, note:String){
        // schedule upcoming refills
        viewModelScope.launch{
            //val medication = _medDao.getMedicationByID(medID).firstOrNull()
            try {
                val nextRefillDate = _calculateNextRefill(refillDays)
                val intent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra("Name", medName)
                    putExtra("Message", "Refill Reminder")
                    putExtra("Note", note)
                }
                val requestCode = _generateUniqueRequestCode()
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                _alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    nextRefillDate,
                    pendingIntent
                )
                _reminderState.value = ReminderState.Success("Reminders set successfully")
            }catch(e:Exception){
                _reminderState.value = ReminderState.Error(e.message ?: "Unknown Error")
            }

        }

    }

    private fun _calculateNextReminder(nextDate:Date, startDate: Date, endDate: Date): List<Long> {
        val reminderDates = mutableListOf<Long>()
        // convert today's time to milliseconds
        val calendar = Calendar.getInstance().apply {
            // retrieve today's date
            set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

            // apply the start time from startDate
            val startDateTime = Calendar.getInstance().apply{
                time = startDate
            }
            set(Calendar.HOUR_OF_DAY, startDateTime.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, startDateTime.get(Calendar.MINUTE))

            // convert to milliseconds
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val reminderTimeCalendar = Calendar.getInstance().apply {
            time = nextDate
        }

        while (!calendar.time.after(endDate)) {
            // set the reminder time (hours and minutes) for the current day
            calendar.set(Calendar.HOUR_OF_DAY, reminderTimeCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, reminderTimeCalendar.get(Calendar.MINUTE))

            // add the calculated time in milliseconds to the list
            reminderDates.add(calendar.timeInMillis)

            // move to the next day
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return reminderDates
    }

    private fun _calculateNextRefill(refillDays:Int): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, refillDays)
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }

        return calendar.timeInMillis
    }

    private fun _generateUniqueRequestCode(): Int{
        return UUID.randomUUID().hashCode()
    }

}