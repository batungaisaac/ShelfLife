package com.example.shelflife.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.math.sqrt

// Listens to the accelerometer. Add it as a lifecycle observer and it
// only runs while the screen is in the foreground.
class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener, DefaultLifecycleObserver {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // False on some emulators and cheap phones, which have no accelerometer
    val isAvailable: Boolean
        get() = accelerometer != null

    // Last force we read, in g. Resting on a table this sits around 1.0
    var lastForce = 0f
        private set

    private var lastShakeTime = 0L

    fun start() {
        accelerometer ?: return
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onResume(owner: LifecycleOwner) = start()

    // No point reading the sensor when nobody can see the app
    override fun onPause(owner: LifecycleOwner) = stop()

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0] / SensorManager.GRAVITY_EARTH
        val y = event.values[1] / SensorManager.GRAVITY_EARTH
        val z = event.values[2] / SensorManager.GRAVITY_EARTH

        lastForce = sqrt(x * x + y * y + z * z)

        if (lastForce < SHAKE_THRESHOLD) return

        // One shake gives a burst of readings over the threshold,
        // so ignore anything that comes in right after the last one
        val now = System.currentTimeMillis()
        if (now - lastShakeTime < SHAKE_COOLDOWN_MS) return

        lastShakeTime = now
        onShake()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // don't need this
    }

    companion object {
        // 2.7g is a common starting point. Much lower and just picking the phone up sets it off
        private const val SHAKE_THRESHOLD = 2.7f
        private const val SHAKE_COOLDOWN_MS = 500L
    }
}
