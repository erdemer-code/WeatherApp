package com.erdemer.weatherapp.model.response


import com.google.gson.annotations.SerializedName

data class AdministrativeArea(
    @SerializedName("ID")
    val iD: String?,
    @SerializedName("LocalizedName")
    val localizedName: String?
)