package com.erdemer.weatherapp.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autoCompleteSearchTable")
data class AutoCompleteSearchRoomModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val cityName: String?,
    val countryName: String?,
    val cityId: String?,
    val countryId: String?,
    val timeStamp: Long?,
    val key: String?
)
