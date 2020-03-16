package com.test.masterthesisclient.models

data class SensorPojo(var timestamp  : Long,
                      var x : Float? = 0.0f,var y : Float? = 0.0f, var z : Float? = 0.0f,
                      var activity: String = ""
                      )