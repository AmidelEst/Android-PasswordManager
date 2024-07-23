package com.ponichTech.pswdManager.data.model

import java.util.UUID

data class User(val userId:String = UUID.randomUUID().toString(),
                val name:String="",
                val email:String="",
                val userPhoto: String? = "",
                val phone:String?="")