package com.test.masterthesisclient.viewmodels

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

class BaseFragmentViewModel : ViewModel(),LifecycleOwner {


    fun textToSpeechConvertor(text : String)
    {

    }

    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }


}