package com.test.masterthesisclient

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


object Network {

    private var client: OkHttpClient
    val MEDIA_TYPE = "application/json".toMediaTypeOrNull()
    init {
        client = OkHttpClient().newBuilder().build()
    }

    fun sendData(dataList : ArrayList<MergedClass>) : LiveData<ResponsePojo>
    {


        val body = Gson().toJson(dataList).toRequestBody(MEDIA_TYPE)

        dataList.clear()

        val responseLiveData = MutableLiveData<ResponsePojo>()

        val request = Request.Builder()
            .method("POST",body)
            .url(Constants.ngrokBaseUrl+"/storeData")
            .post(body)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()


        client.newCall(request).enqueue(object  : Callback{
            override fun onFailure(call: Call, e: IOException) {
                responseLiveData.postValue(null)
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    responseLiveData.postValue(
                        Gson().fromJson(
                            response.body?.string(),
                            ResponsePojo::class.java
                        )
                    )
                }catch (e : Exception)
                {
                    e.printStackTrace()
                }
            }

        })

        return responseLiveData
    }

}