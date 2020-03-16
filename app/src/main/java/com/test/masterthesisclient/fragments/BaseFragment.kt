package com.test.masterthesisclient.fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.test.masterthesisclient.Network

import com.test.masterthesisclient.R
import com.test.masterthesisclient.databinding.FragmentBaseBinding
import com.test.masterthesisclient.models.MergedClass
import com.test.masterthesisclient.models.SensorPojo
import com.test.masterthesisclient.viewmodels.BaseFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BaseFragment : Fragment() {

    private var dataListAcc = ArrayList<SensorPojo>()
    private var dataListGy = ArrayList<SensorPojo>()
    private var mergedClass = ArrayList<MergedClass>()
    private var recording = false
    private var actionType = "LYINGFLAT"
    private var actionArray = arrayOf("LYINGFLAT", "WALKING", "RUNNING", "SITTING", "STANDING")
    private var time = 0


    private lateinit var sensorManager : SensorManager
    private lateinit var accelerometer : Sensor
    private lateinit var gyroscope : Sensor
    private var vibrateThreshold = 0.0f
    private lateinit var databinding : FragmentBaseBinding

    private var accelerometerListner = object : SensorEventListener2
    {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onFlushCompleted(p0: Sensor?) {

        }

        override fun onSensorChanged(p0: SensorEvent?) {
            if(recording)
                dataListAcc.add(
                    SensorPojo(
                        x = p0?.values?.get(0),
                        y = p0?.values?.get(1), activity = actionType,
                        z = p0?.values?.get(2), timestamp = System.currentTimeMillis()
                    )
                )
        }

    }

    private var gyroscopeListner = object : SensorEventListener2
    {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onFlushCompleted(p0: Sensor?) {

        }

        override fun onSensorChanged(p0: SensorEvent?) {
            if(recording)
                dataListGy.add(
                    SensorPojo(
                        x = p0?.values?.get(0),
                        y = p0?.values?.get(1), activity = actionType,
                        z = p0?.values?.get(2), timestamp = System.currentTimeMillis()
                    )
                )
        }

    }

    private fun processData()
    {

        var count = minOf(dataListAcc.size, dataListGy.size )

        for (i in 0 until count)
        {
            mergedClass.add(
                MergedClass(
                    actionType, dataListAcc[i].timestamp,
                    dataListAcc[i].x, dataListAcc[i].y, dataListAcc[i].z,
                    dataListGy[i].x, dataListGy[i].x, dataListGy[i].x

                )
            )
        }

        dataListAcc.clear()
        dataListGy.clear()

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = DataBindingUtil.inflate(inflater,R.layout.fragment_base,container,false)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager



        databinding.baseFragmentViewModel = ViewModelProviders.of(this)[BaseFragmentViewModel::class.java]

        if(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!=null)
        {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            sensorManager.registerListener(accelerometerListner, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(gyroscopeListner,gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        }

        databinding.tvOutTest.setOnClickListener {

            recording = true
            databinding.tvOutTest.text = "Recording ..."
            databinding.tvOutTest.isEnabled = false
            databinding.tvOutPred.isEnabled = false


            GlobalScope.launch(Dispatchers.Main) {
                delay(time*1000.toLong())
                recording = false

                processData()

                Log.d("Samples",mergedClass.size.toString())
                databinding.tvOutTest.text = "Record"
                databinding.tvOutTest.isEnabled = true
                databinding.tvOutPred.isEnabled = true
                //AlertDialog.Builder(activity).setMessage(dataListAcc.toString()).show()

                Network.sendData(mergedClass,"REC").observe(viewLifecycleOwner, Observer {
                    try {
                        Toast.makeText(requireContext(), it.payload, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
            }

        }

        databinding.tvOutPred.setOnClickListener {

            recording = true
            databinding.tvOutTest.text = "Predicting..."
            databinding.tvOutTest.isEnabled = false
            databinding.tvOutPred.isEnabled = false



            GlobalScope.launch(Dispatchers.Main) {
                delay(time*1000.toLong())
                recording = false

                processData()

                Log.d("Samples",mergedClass.size.toString())
                databinding.tvOutPred.text = "Predict"
                databinding.tvOutTest.isEnabled = true
                databinding.tvOutPred.isEnabled = true
                //AlertDialog.Builder(activity).setMessage(dataListAcc.toString()).show()

                Network.sendData(mergedClass,"PRED").observe(viewLifecycleOwner, Observer {
                    try {
                        Toast.makeText(requireContext(), it.payload, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
            }
        }



        var adapter = ArrayAdapter<String>(requireContext(),R.layout.support_simple_spinner_dropdown_item,actionArray)
        databinding.spinner.adapter = adapter
        databinding.spinner.onItemSelectedListener = object :  AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                actionType = actionArray[p2]
            }

        }

        databinding.seekBar.progress = 0
        databinding.textView.text = "Timer : - ${time}s"
        databinding.seekBar.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                time = p0?.progress!! * 3
                databinding.textView.text = "Timer : - ${time}s"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        return  databinding.root
    }



    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(accelerometerListner, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(gyroscopeListner,gyroscope, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(gyroscopeListner)
        sensorManager.unregisterListener(accelerometerListner)

    }
}
