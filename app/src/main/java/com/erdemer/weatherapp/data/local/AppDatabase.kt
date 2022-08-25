package com.erdemer.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erdemer.weatherapp.model.local.AutoCompleteSearchRoomModel

@Database(entities = [AutoCompleteSearchRoomModel::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun autoCompleteSearchDao(): AutoCompleteSearchDao
}