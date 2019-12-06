package com.test.masterthesisclient

data class SensorPojo(var timestamp  : Long = System.currentTimeMillis(),
                      var x : Float?,var y : Float?, var z : Float?,
                      var activity: String = ""
                      )