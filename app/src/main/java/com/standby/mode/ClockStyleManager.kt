package com.standby.mode

import android.content.Context
import android.content.SharedPreferences

class ClockStyleManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    enum class ClockStyle {
        DIGITAL,
        BOLD,
        BOLD_SQUARE,
        BOLD_CONDENSED,
        NEON,
        MINIMAL,
        OUTLINED,
        RETRO
    }

    fun saveClockStyle(style: ClockStyle) {
        prefs.edit().putString(KEY_CLOCK_STYLE, style.name).apply()
    }

    fun getClockStyle(): ClockStyle {
        val styleName = prefs.getString(KEY_CLOCK_STYLE, ClockStyle.DIGITAL.name)
        return try {
            ClockStyle.valueOf(styleName ?: ClockStyle.DIGITAL.name)
        } catch (e: IllegalArgumentException) {
            ClockStyle.DIGITAL
        }
    }

    companion object {
        private const val PREFS_NAME = "StandByPrefs"
        private const val KEY_CLOCK_STYLE = "clock_style"

        @Volatile
        private var instance: ClockStyleManager? = null

        fun getInstance(context: Context): ClockStyleManager {
            return instance ?: synchronized(this) {
                instance ?: ClockStyleManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
