package com.test.masterthesisclient.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.masterthesisclient.config.Constants

class SharedViewModel : ViewModel(){

    private var host : MutableLiveData<String> = MutableLiveData()

    fun saveHost(context: Context,host:String)
    {
        var editor = context.getSharedPreferences(Constants.SP,Context.MODE_PRIVATE)
        editor.edit().putString("host",host).apply()
        Constants.ngrokBaseUrl = host
        this.host.value = host

    }

    fun getHost(context: Context) : MutableLiveData<String>
    {
        var editor = context.getSharedPreferences(Constants.SP,Context.MODE_PRIVATE).getString("host",Constants.ngrokBaseUrl)

        Constants.ngrokBaseUrl = editor.toString()

        this.host.value = editor
        return host
    }

}