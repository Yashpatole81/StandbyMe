package com.standby.mode

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.standby.mode.databinding.ActivityClockLandscapePreviewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClockLandscapePreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClockLandscapePreviewBinding
    private lateinit var clockStyleManager: ClockStyleManager
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeUpdateRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClockLandscapePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clockStyleManager = ClockStyleManager.getInstance(this)

        hideSystemUI()
        startClock()
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

    private fun startClock() {
        val style = clockStyleManager.getClockStyle()
        
        timeUpdateRunnable = object : Runnable {
            override fun run() {
                updateClock(style)
                updateDate()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timeUpdateRunnable)
    }

    private fun updateClock(style: ClockStyleManager.ClockStyle) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        binding.landscapeClockText.text = currentTime
        
        applyClockStyle(binding.landscapeClockText, style)
    }

    private fun updateDate() {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        binding.landscapeDateText.text = currentDate
    }

    private fun applyClockStyle(textView: android.widget.TextView, style: ClockStyleManager.ClockStyle) {
        when (style) {
            ClockStyleManager.ClockStyle.DIGITAL -> {
                textView.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
                textView.textSize = 120f
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.BOLD -> {
                textView.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
                textView.textSize = 140f
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.BOLD_SQUARE -> {
                textView.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
                textView.textSize = 140f
                textView.letterSpacing = 0.15f
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.BOLD_CONDENSED -> {
                textView.typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
                textView.textSize = 140f
                textView.letterSpacing = -0.05f
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.NEON -> {
                textView.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                textView.textSize = 120f
                textView.setTextColor(ContextCompat.getColor(this, R.color.neon_cyan))
                textView.setShadowLayer(40f, 0f, 0f, ContextCompat.getColor(this, R.color.neon_cyan))
            }
            ClockStyleManager.ClockStyle.MINIMAL -> {
                textView.typeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
                textView.textSize = 110f
                textView.setTextColor(ContextCompat.getColor(this, R.color.minimal_gray))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.OUTLINED -> {
                textView.typeface = Typeface.create("sans-serif", Typeface.BOLD)
                textView.textSize = 120f
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                textView.setShadowLayer(15f, 0f, 0f, ContextCompat.getColor(this, R.color.minimal_gray))
            }
            ClockStyleManager.ClockStyle.RETRO -> {
                textView.typeface = Typeface.MONOSPACE
                textView.textSize = 110f
                textView.setTextColor(ContextCompat.getColor(this, R.color.retro_amber))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timeUpdateRunnable.isInitialized) {
            handler.removeCallbacks(timeUpdateRunnable)
        }
    }
}
