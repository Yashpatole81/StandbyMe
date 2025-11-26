package com.standby.mode

import android.os.Handler
import android.os.Looper

class TimerManager(private val callback: TimerCallback) {

    private var handler: Handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var remainingSeconds: Int = 0
    private var isRunning: Boolean = false

    interface TimerCallback {
        fun onTick(remainingSeconds: Int)
        fun onFinish()
    }

    fun start(totalSeconds: Int) {
        if (totalSeconds <= 0) return
        
        remainingSeconds = totalSeconds
        isRunning = true
        
        timerRunnable = object : Runnable {
            override fun run() {
                if (remainingSeconds > 0) {
                    callback.onTick(remainingSeconds)
                    remainingSeconds--
                    handler.postDelayed(this, 1000)
                } else {
                    isRunning = false
                    callback.onFinish()
                }
            }
        }
        
        handler.post(timerRunnable!!)
    }

    fun stop() {
        isRunning = false
        timerRunnable?.let { handler.removeCallbacks(it) }
        timerRunnable = null
    }

    fun isRunning(): Boolean = isRunning

    fun getRemainingSeconds(): Int = remainingSeconds
}
