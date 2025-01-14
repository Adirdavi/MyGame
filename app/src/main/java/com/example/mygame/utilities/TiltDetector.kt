package com.example.mygame.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.mygame.interfaces.TiltCallback

class TiltDetector(context: Context, private val tiltCallback: TiltCallback?) {
    private val TAG = "TiltDetector"
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private lateinit var sensorEventListener: SensorEventListener

    companion object {
        private const val TILT_THRESHOLD = 3.0f
        private const val TIME_THRESHOLD = 500L
    }

    private var timestamp: Long = 0L

    init {

        initSensor()
    }

    private fun initSensor() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                detectTilt(x)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    private fun detectTilt(x: Float) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - timestamp >= TIME_THRESHOLD) {
            when {
                x >= TILT_THRESHOLD -> {
                    timestamp = currentTime
                    tiltCallback?.tiltX(-1)
                }
                x <= -TILT_THRESHOLD -> {
                    timestamp = currentTime

                    tiltCallback?.tiltX(1)
                }
            }
        }
    }

    fun start() {
        sensor?.let {
            sensorManager.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
            Log.d(TAG, "Sensor started")
        }
    }

    fun stop() {
        sensor?.let {
            sensorManager.unregisterListener(sensorEventListener)

        }
    }
}