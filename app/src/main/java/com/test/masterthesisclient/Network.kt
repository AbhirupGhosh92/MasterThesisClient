package com.test.masterthesisclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.test.masterthesisclient.config.Constants
import com.test.masterthesisclient.models.MergedClass
import com.test.masterthesisclient.models.ResponsePojo
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


object Network {

    private var client: OkHttpClient
    val MEDIA_TYPE = "application/json".toMediaTypeOrNull()
    init {
        client = OkHttpClient().newBuilder().build()
    }

    fun storeData(dataList : ArrayList<MergedClass>, type : String) : LiveData<ResponsePojo>
    {

        val json = JSONObject()
        json.put("type",type)
        json.put("data",Gson().toJson(dataList))


        val body = json.toString().toRequestBody(MEDIA_TYPE)

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
                var body = response.body?.string().toString()
                try {


                    responseLiveData.postValue(
                        Gson().fromJson(
                            body,
                            ResponsePojo::class.java
                        )
                    )
                }catch (e : Exception)
                {
                    e.printStackTrace()
                    responseLiveData.postValue(ResponsePojo("ERROR",response.code.toString(),body,response.message))
                }
            }

        })

        return responseLiveData
    }

    fun trainModel(dataList : ArrayList<MergedClass>, type : String) : LiveData<ResponsePojo>
    {

        val json = JSONObject()
        json.put("type",type)
        json.put("data",Gson().toJson(dataList))


        val body = json.toString().toRequestBody(MEDIA_TYPE)

        dataList.clear()

        val responseLiveData = MutableLiveData<ResponsePojo>()

        val request = Request.Builder()
            .method("POST",body)
            .url(Constants.ngrokBaseUrl+"/trainModel")
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
                var body = response.body?.string().toString()
                try {


                    responseLiveData.postValue(
                        Gson().fromJson(
                            body,
                            ResponsePojo::class.java
                        )
                    )
                }catch (e : Exception)
                {
                    e.printStackTrace()
                    responseLiveData.postValue(ResponsePojo("ERROR",response.code.toString(),body,response.message))
                }
            }

        })

        return responseLiveData
    }

    fun predict(dataList : ArrayList<MergedClass>, type : String) : LiveData<ResponsePojo>
    {

        val json = JSONObject()
        json.put("type",type)
        json.put("data",Gson().toJson(dataList))


        val body = json.toString().toRequestBody(MEDIA_TYPE)

        dataList.clear()

        val responseLiveData = MutableLiveData<ResponsePojo>()

        val request = Request.Builder()
            .method("POST",body)
            .url(Constants.ngrokBaseUrl+"/predict")
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
                var body = response.body?.string().toString()
                try {


                    responseLiveData.postValue(
                        Gson().fromJson(
                            body,
                            ResponsePojo::class.java
                        )
                    )
                }catch (e : Exception)
                {
                    e.printStackTrace()
                    responseLiveData.postValue(ResponsePojo("ERROR",response.code.toString(),body,response.message))
                }
            }

        })

        return responseLiveData
    }

}