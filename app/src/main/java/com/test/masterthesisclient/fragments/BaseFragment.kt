package com.test.masterthesisclient.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.test.masterthesisclient.Network
import com.test.masterthesisclient.R
import com.test.masterthesisclient.config.Constants
import com.test.masterthesisclient.databinding.FragmentBaseBinding
import com.test.masterthesisclient.models.MergedClass
import com.test.masterthesisclient.models.SensorPojo
import com.test.masterthesisclient.viewmodels.BaseFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class BaseFragment : Fragment() {

    private val RC_SIGN_IN: Int = 1332
    private var dataListAcc = ArrayList<SensorPojo>()
    private var dataListGy = ArrayList<SensorPojo>()
    private var mergedClass = ArrayList<MergedClass>()
    private var recording = false
    private var actionType = "LYINGFLAT"
    private var actionArray = arrayOf("LYINGFLAT", "WALKING", "RUNNING",
        "SITTING", "STANDING",
        "CLIMBING_UP_STAIRS",
        "CLIMBING_DOWN_STAIRS")
    private var time = 0
    private var STATE = ""
    private lateinit var textToSpeech : TextToSpeech
    private var firebaseUser : FirebaseUser? = null



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
            if(recording) {
                dataListAcc.add(
                    SensorPojo(
                        x = p0?.values?.get(0),
                        y = p0?.values?.get(1), activity = actionType,
                        z = p0?.values?.get(2), timestamp = System.currentTimeMillis()
                    )
                )

                if(dataListAcc.size >= 104)
                {
                    recording = false
                    captureData()
                }

            }
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
                    databinding.spinner.text.toString(), dataListAcc[i].timestamp,
                    dataListAcc[i].x, dataListAcc[i].y, dataListAcc[i].z,
                    dataListGy[i].x, dataListGy[i].y, dataListGy[i].z
                )
            )
        }

        dataListAcc.clear()
        dataListGy.clear()

    }

    private fun captureData()
    {

        for (i in 0 until 100)
        {
            mergedClass.add(
                MergedClass(
                    databinding.spinner.text.toString(), dataListAcc[i].timestamp,
                    dataListAcc[i].x, dataListAcc[i].y, dataListAcc[i].z,
                    dataListGy[i].x, dataListGy[i].y, dataListGy[i].z

                )
            )
        }

        sendPrediction()


    }


    private fun sendPrediction()
    {
        Log.d("Samples", mergedClass.size.toString())
        //AlertDialog.Builder(activity).setMessage(dataListAcc.toString()).show()

        Network.predict(mergedClass, STATE,firebaseUser?.uid.toString())
            .observe(viewLifecycleOwner, Observer {
                try {



                    mergedClass.clear()
                    dataListAcc.clear()
                    dataListGy.clear()

                    recording = true

                    if (it.code.equals("200")) {
                        databinding.tvOutTest.isEnabled = true
                        databinding.tvOutTest.visibility = View.VISIBLE
                        databinding.textView.text = it.payload
//                                textToSpeech.speak("Server error",TextToSpeech.QUEUE_FLUSH,
//                                    null,null)

                        Toast.makeText(
                            requireContext(),
                            "Data Saved",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            textToSpeech.speak(
                                it.payload, TextToSpeech.QUEUE_FLUSH,
                                null, null
                            )
                        }
                    } else if (it.code == "503") {
                        Toast.makeText(
                            requireContext(),
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            textToSpeech.speak(
                                it.message, TextToSpeech.QUEUE_FLUSH,
                                null, null
                            )
                        }
                        databinding.tvOutTest.isEnabled = true
                        databinding.tvOutTest.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Server error",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            textToSpeech.speak(
                                "Server error", TextToSpeech.QUEUE_FLUSH,
                                null, null

                            )
                        }
                    }

                    databinding.tvOutTest.isEnabled = true
                    databinding.tvOutTest.visibility = View.VISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser == null)
        {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                    .build(),
                RC_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode)
        {
            RC_SIGN_IN -> {
                val response = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    // Successfully signed in
                    firebaseUser = FirebaseAuth.getInstance().currentUser
                    // ...
                } else {
                    Toast.makeText(requireContext(),"Sign In Failed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = DataBindingUtil.inflate(inflater,R.layout.fragment_base,container,false)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        textToSpeech = TextToSpeech(requireContext()){
            if(it != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.UK)
            }
        }

        databinding.edtServerUrl.addTextChangedListener {

            if(it.isNullOrEmpty().not())
            Constants.ngrokBaseUrl = it.toString()
        }

        databinding.baseFragmentViewModel = ViewModelProviders.of(this)[BaseFragmentViewModel::class.java]

        if(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!=null)
        {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            sensorManager.registerListener(accelerometerListner, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(gyroscopeListner,gyroscope, SensorManager.SENSOR_DELAY_UI)
        }


            databinding.swSelector.setOnCheckedChangeListener { compoundButton, b ->


                if(databinding.swSelector.isChecked)
                {
                    databinding.tvSection.text = "PREDICTION"
                    STATE = "PRED"
                    databinding.seekBar.visibility = View.GONE
                    databinding.textView.text = ""
                    databinding.spinner.visibility = View.GONE
                }
                else
                {
                    databinding.tvSection.text = "TRAINING"
                    STATE = "REC"
                    databinding.seekBar.visibility = View.VISIBLE
                    databinding.textView.text = "Timer : - ${time}s"
                    databinding.spinner.visibility = View.VISIBLE
                }


            }


        databinding.tvOutTest.setOnClickListener {


            textToSpeech.speak("Starting in 3 seconds",TextToSpeech.QUEUE_FLUSH,
                null,null
            )

            if(databinding.swSelector.isChecked.not()) {
                databinding.tvOutTest.isEnabled = false

                databinding.tvOutTest.visibility = View.GONE

                GlobalScope.launch(Dispatchers.Main) {


                    Toast.makeText(requireContext(),"Starting in 3 seconds",Toast.LENGTH_SHORT).show()
                    for(i in 0..2)
                    {

                        delay(1000)
                    }

                    recording = true

                    delay(time * 1000.toLong())
                    recording = false

                    processData()

                    Log.d("Samples", mergedClass.size.toString())
                    //AlertDialog.Builder(activity).setMessage(dataListAcc.toString()).show()


                    Network.storeData(mergedClass,firebaseUser?.uid.toString()).observe(viewLifecycleOwner, Observer {
                        try {


                            if(it.code.equals("200")) {
                                databinding.tvOutTest.isEnabled = true
                                databinding.tvOutTest.visibility = View.VISIBLE

                                val v: Vibrator =
                                   context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    v.vibrate(
                                        VibrationEffect.createOneShot(
                                            500,
                                            VibrationEffect.DEFAULT_AMPLITUDE
                                        )
                                    )
                                } else {
                                    //deprecated in API 26
                                    v.vibrate(500)
                                }

                                Toast.makeText(requireContext(),"Data Saved",Toast.LENGTH_SHORT).show()

                                textToSpeech.speak("Data Saved",TextToSpeech.QUEUE_FLUSH,
                                    null,null)
                            }
                            else if(it.code == "503")
                            {
                                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                                textToSpeech.speak(it.message,TextToSpeech.QUEUE_FLUSH,
                                    null,null)
                            }
                            else
                            {
                                Toast.makeText(requireContext(),"Server error",Toast.LENGTH_SHORT).show()
                                textToSpeech.speak("Server error",TextToSpeech.QUEUE_FLUSH,
                                    null,null)
                            }

                            databinding.tvOutTest.isEnabled = true
                            databinding.tvOutTest.visibility = View.VISIBLE

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                }
            }
            else
            {

                recording = !recording

                if(recording)
                {
                    databinding.tvOutTest.text = "RECORDING..."
                }
                else
                {
                    databinding.tvOutTest.text = "RECORD"
                }
            }

        }




        var adapter = ArrayAdapter<String>(requireContext(),R.layout.support_simple_spinner_dropdown_item,actionArray)

        databinding.spinner.setAdapter(adapter)
        databinding.spinner.addTextChangedListener {enteredText ->

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
