package com.imdumb.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CastCreditDto(@SerializedName("person") val person: PersonDto?)

data class PersonDto(@SerializedName("id") val id: Long?, @SerializedName("name") val name: String?)
