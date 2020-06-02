package hikmah.nur.mytodo.notification

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import hikmah.nur.mytodo.data.database.TodoRecord
import hikmah.nur.mytodo.utils.stringToAscii
import java.util.*

class NotificationUtils {
    fun setNotification(todoRecord: TodoRecord,activity: Activity){
        if (todoRecord.dueTime!!>0){
            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager

            val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java)

            alarmIntent.putExtra("reason","notification")
            alarmIntent.putExtra("timestamp", todoRecord.dueTime)

            var calendar = Calendar.getInstance()
            calendar.timeInMillis = todoRecord.dueTime

            val  pendingIntent = PendingIntent.getBroadcast(
                activity,
                stringToAscii(todoRecord.title),
                alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.timeInMillis, pendingIntent)
        }
    }

    fun cancelNotification(todoRecord: TodoRecord, activity: Activity){
        if (todoRecord.dueTime!! > 0){
            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager

            val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java)

            alarmIntent.putExtra("reason","notification")
            alarmIntent.putExtra("timestamp", todoRecord.dueTime)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = todoRecord.dueTime

            val pendingIntent = PendingIntent.getBroadcast(
                activity,
                stringToAscii(todoRecord.title),
                alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}