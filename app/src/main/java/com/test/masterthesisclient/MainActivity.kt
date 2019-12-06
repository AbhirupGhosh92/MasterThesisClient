package com.test.masterthesisclient

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.test.masterthesisclient.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() , SensorEventListener2,CoroutineScope by MainScope() {

    private var dataList = ArrayList<SensorPojo>()
    private var recording = false

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onFlushCompleted(p0: Sensor?) {

    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if(recording)
            dataList.add(SensorPojo(x = p0?.values?.get(0),
                y = p0?.values?.get(1),
                z = p0?.values?.get(2)))
    }

    private lateinit var sensorManager : SensorManager
    private lateinit var accelerometer : Sensor
    private var vibrateThreshold = 0.0f
    private lateinit var databinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val activity = this
        if(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!=null)
        {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            vibrateThreshold = accelerometer.maximumRange / 2
        }

        databinding.tvOut.setOnClickListener {

            recording = true
            databinding.tvOut.text = "Recording ..."
            databinding.tvOut.isEnabled = false


           launch(Dispatchers.Main) {
               delay(2000)
               recording = false
               databinding.tvOut.text = "Recording"
               databinding.tvOut.isEnabled = true
               AlertDialog.Builder(activity).setMessage(dataList.toString()).show()

               Network.sendData(dataList).observe(this@MainActivity, Observer {
                       try {
                           Toast.makeText(activity, it.payload, Toast.LENGTH_SHORT).show()
                       } catch (e: Exception) {
                           e.printStackTrace()
                       }
               })
           }

        }

    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)

    }
}
