package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.data.model.ReminderDoc
import java.util.Calendar

object ReminderScheduler {

    fun syncReminders(context: Context, reminders: List<ReminderDoc>) {
        ReminderBroadcastReceiver.ensureChannel(context)
        reminders.forEach { reminder ->
            if (reminder.enabled) {
                scheduleDaily(context, reminder)
            } else {
                cancel(context, reminder.id)
            }
        }
    }

    fun scheduleDaily(context: Context, reminder: ReminderDoc) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestCode = reminder.id.hashCode()
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(ReminderBroadcastReceiver.EXTRA_TITLE, reminder.title)
            putExtra(ReminderBroadcastReceiver.EXTRA_BODY, "أذكاري — ${reminder.title}")
            putExtra(ReminderBroadcastReceiver.EXTRA_ID, requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminder.hour.toInt().coerceIn(0, 23))
            set(Calendar.MINUTE, reminder.minute.toInt().coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancel(context: Context, reminderId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestCode = reminderId.hashCode()
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
