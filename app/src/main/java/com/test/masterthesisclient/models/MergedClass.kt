package com.test.masterthesisclient.models

data class MergedClass(
                        var activity : String,
                        var timestamp  : Long,
                        var lin_x : Float? = 0.0f ,
                       var lin_y : Float? = 0.0f,
                       var lin_z : Float? = 0.0f,
                       var gy_x : Float? = 0.0f,
                       var gy_y : Float? = 0.0f,
                       var gy_z : Float? = 0.0f)