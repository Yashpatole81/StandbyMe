package com.standby.mode

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.standby.mode.databinding.ActivityTimerLandscapeBinding

class TimerLandscapeActivity : AppCompatActivity(), TimerManager.TimerCallback {

    private lateinit var binding: ActivityTimerLandscapeBinding
    private lateinit var timerManager: TimerManager
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerLandscapeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()
        acquireWakeLock()

        val totalSeconds = intent.getIntExtra(TimerSetupActivity.EXTRA_TOTAL_SECONDS, 0)
        
        timerManager = TimerManager(this)
        timerManager.start(totalSeconds)

        binding.stopButton.setOnClickListener {
            stopTimer()
        }
    }

    override fun onTick(remainingSeconds: Int) {
        val displayTime = formatTime(remainingSeconds)
        binding.countdownText.text = displayTime
    }

    override fun onFinish() {
        binding.countdownText.text = getString(R.string.timer_finished)
        releaseWakeLock()
        
        // Auto-finish after showing "Time's Up!" for 2 seconds
        binding.countdownText.postDelayed({
            finish()
        }, 2000)
    }

    private fun stopTimer() {
        timerManager.stop()
        releaseWakeLock()
        finish()
    }

    private fun formatTime(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun acquireWakeLock() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "StandByMode::TimerWakeLock"
        ).apply {
            acquire(24 * 60 * 60 * 1000L) // Max 24 hours
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

    override fun onDestroy() {
        super.onDestroy()
        timerManager.stop()
        releaseWakeLock()
    }
}
