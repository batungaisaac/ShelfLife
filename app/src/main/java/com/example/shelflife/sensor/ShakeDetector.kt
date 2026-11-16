package com.example.shelflife.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

// Listens to the accelerometer. Not hooked up to any screen yet.
class ShakeDetector(context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Last force we read, in g. Resting on a table this sits around 1.0
    var lastForce = 0f
        private set

    fun start() {
        // Some emulators and cheap phones have no accelerometer
        accelerometer ?: return
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0] / SensorManager.GRAVITY_EARTH
        val y = event.values[1] / SensorManager.GRAVITY_EARTH
        val z = event.values[2] / SensorManager.GRAVITY_EARTH

        lastForce = sqrt(x * x + y * y + z * z)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // don't need this
    }
}
