package com.standby.mode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PowerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("PowerReceiver", "Received action: ${intent.action}")
        
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                Log.d("PowerReceiver", "Power connected - launching StandBy mode")
                val activityIntent = Intent(context, StandByModeActivity::class.java)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(activityIntent)
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                Log.d("PowerReceiver", "Power disconnected - activity will close itself")
                // The activity will handle its own closing
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("PowerReceiver", "Device booted - receiver is now active")
                // App is now ready to receive power events after boot
            }
        }
    }
}