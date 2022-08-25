package com.erdemer.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.erdemer.weatherapp.model.local.AutoCompleteSearchRoomModel

@Dao
interface AutoCompleteSearchDao {
    @Query("SELECT * FROM autoCompleteSearchTable ORDER BY timeStamp DESC LIMIT 5")
    suspend fun getAllSearchResult(): List<AutoCompleteSearchRoomModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearchResult(autoCompleteSearchRoomModel: AutoCompleteSearchRoomModel)
}