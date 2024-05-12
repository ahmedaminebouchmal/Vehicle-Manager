package com.technoapps.vehiclemanager.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service1 = Intent(context, AlarmService::class.java)
        context.startService(service1)
    }
}