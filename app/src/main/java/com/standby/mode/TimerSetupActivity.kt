package com.standby.mode

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.standby.mode.databinding.ActivityTimerSetupBinding

class TimerSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupNumberPickers()
        setupStartButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupNumberPickers() {
        // Hours picker (0-23)
        binding.hoursPicker.minValue = 0
        binding.hoursPicker.maxValue = 23
        binding.hoursPicker.value = 0
        binding.hoursPicker.wrapSelectorWheel = true

        // Minutes picker (0-59)
        binding.minutesPicker.minValue = 0
        binding.minutesPicker.maxValue = 59
        binding.minutesPicker.value = 0
        binding.minutesPicker.wrapSelectorWheel = true

        // Seconds picker (0-59)
        binding.secondsPicker.minValue = 0
        binding.secondsPicker.maxValue = 59
        binding.secondsPicker.value = 0
        binding.secondsPicker.wrapSelectorWheel = true
    }

    private fun setupStartButton() {
        binding.startButton.setOnClickListener {
            val hours = binding.hoursPicker.value
            val minutes = binding.minutesPicker.value
            val seconds = binding.secondsPicker.value

            val totalSeconds = (hours * 3600) + (minutes * 60) + seconds

            if (totalSeconds > 0) {
                val intent = Intent(this, TimerLandscapeActivity::class.java)
                intent.putExtra(EXTRA_TOTAL_SECONDS, totalSeconds)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please set a time greater than 0", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_TOTAL_SECONDS = "extra_total_seconds"
    }
}
