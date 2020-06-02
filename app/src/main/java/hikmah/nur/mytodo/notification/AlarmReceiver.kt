package hikmah.nur.mytodo.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val service = Intent(context, NotificationSercive::class.java)
        service.putExtra("reason", intent?.getStringExtra("reason"))
        service.putExtra("timestampt", intent?.getLongExtra("timestampt", 0))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(service)
        } else {
            context?.startService(service)
        }
    }
}