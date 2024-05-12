package com.technoapps.vehiclemanager.notifications

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.activities.MainActivity

class AlarmService : IntentService("AlarmService") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            val context = this.applicationContext
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mIntent = Intent(context, MainActivity::class.java)
            mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val pendingIntent =
                PendingIntent.getActivity(context, 0, mIntent,
                    PendingIntent.FLAG_MUTABLE)
            val channelId = "11111"
            try {
                val channelName = context.resources.getString(R.string.app_name)
                val channelDescription = "Application_name Alert"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val mChannel = NotificationChannel(channelId, channelName, importance)
                    mChannel.description = channelDescription
                    mChannel.enableVibration(true)
                    notificationManager.createNotificationChannel(mChannel)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val icon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
            val builder = NotificationCompat.Builder(this, channelId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.mipmap.ic_launcher)
                builder.color = context.resources.getColor(R.color.colorTheme)
            } else {
                builder.setSmallIcon(R.mipmap.ic_launcher)
            }
            builder.setContentTitle(context.resources.getString(R.string.app_name))
            builder.setContentText("Stay tuned with us and enjoy App !")
            //builder.setSmallIcon(R.drawable.app_icon);
            builder.setLargeIcon(icon)
            builder.setAutoCancel(true)
            builder.setContentIntent(pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}