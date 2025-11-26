package com.standby.mode

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.standby.mode.databinding.ActivityStandbyModeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StandByModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStandbyModeBinding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeUpdateRunnable: Runnable
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var clockStyleManager: ClockStyleManager

    private val powerDisconnectedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_POWER_DISCONNECTED) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStandbyModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clockStyleManager = ClockStyleManager.getInstance(this)

        setupWindowFlags()
        hideSystemUI()
        acquireWakeLock()

        timeUpdateRunnable = object : Runnable {
            override fun run() {
                updateClock()
                updateDate()
                updateBatteryStatus()
                handler.postDelayed(this, 1000)
            }
        }

        // Register receiver for power disconnection
        val filter = IntentFilter(Intent.ACTION_POWER_DISCONNECTED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(powerDisconnectedReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(powerDisconnectedReceiver, filter)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        handler.post(timeUpdateRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(timeUpdateRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(powerDisconnectedReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver was not registered
        }
        releaseWakeLock()
    }

    private fun setupWindowFlags() {
        // Modern approach for Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        // Keep screen on while activity is visible
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
            PowerManager.ACQUIRE_CAUSES_WAKEUP or
            PowerManager.ON_AFTER_RELEASE,
            "StandByMode::WakeLock"
        ).apply {
            acquire(10 * 60 * 1000L) // 10 minutes max
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 and above
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Android 10 and below
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun updateClock() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        binding.clockText.text = currentTime
        
        // Apply selected clock style
        applyClockStyle(clockStyleManager.getClockStyle())
    }
    
    private fun applyClockStyle(style: ClockStyleManager.ClockStyle) {
        when (style) {
            ClockStyleManager.ClockStyle.DIGITAL -> {
                binding.clockText.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
                binding.clockText.textSize = 96f
                binding.clockText.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                binding.clockText.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.BOLD -> {
                binding.clockText.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
                binding.clockText.textSize = 110f
                binding.clockText.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                binding.clockText.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.BOLD_SQUARE -> {
                binding.clockText.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
                binding.clockText.textSize = 110f
                binding.clockText.letterSpacing = 0.15f
                binding.clockText.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                binding.clockText.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.BOLD_CONDENSED -> {
                binding.clockText.typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
                binding.clockText.textSize = 110f
                binding.clockText.letterSpacing = -0.05f
                binding.clockText.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                binding.clockText.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.NEON -> {
                binding.clockText.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                binding.clockText.textSize = 100f
                binding.clockText.setTextColor(ContextCompat.getColor(this, R.color.neon_cyan))
                binding.clockText.setShadowLayer(30f, 0f, 0f, ContextCompat.getColor(this, R.color.neon_cyan))
            }
            ClockStyleManager.ClockStyle.MINIMAL -> {
                binding.clockText.typeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
                binding.clockText.textSize = 90f
                binding.clockText.setTextColor(ContextCompat.getColor(this, R.color.minimal_gray))
                binding.clockText.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.OUTLINED -> {
                binding.clockText.typeface = Typeface.create("sans-serif", Typeface.BOLD)
                binding.clockText.textSize = 96f
                binding.clockText.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                binding.clockText.setShadowLayer(12f, 0f, 0f, ContextCompat.getColor(this, R.color.minimal_gray))
            }
            ClockStyleManager.ClockStyle.RETRO -> {
                binding.clockText.typeface = Typeface.MONOSPACE
                binding.clockText.textSize = 88f
                binding.clockText.setTextColor(ContextCompat.getColor(this, R.color.retro_amber))
                binding.clockText.setShadowLayer(0f, 0f, 0f, 0)
            }
        }
    }

    private fun updateDate() {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        binding.dateText.text = currentDate
    }

    private fun updateBatteryStatus() {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        if (batteryManager != null) {
            val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            val isCharging = batteryManager.isCharging
            
            val status = if (isCharging) "Charging" else "Not Charging"
            binding.batteryText.text = "$status — $batteryLevel%"
        } else {
            binding.batteryText.text = "Battery info unavailable"
        }
    }
}