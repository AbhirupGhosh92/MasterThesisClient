package com.test.masterthesisclient.models

import com.google.firebase.auth.FirebaseUser

data class RequestPojo(
    var userID : String,
    var user : FirebaseUser,
    var opType : Boolean,
    var store : Boolean ,
    var  payload : String
)