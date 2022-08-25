package com.erdemer.weatherapp.repository

import com.erdemer.weatherapp.data.local.AutoCompleteSearchDao
import com.erdemer.weatherapp.model.local.AutoCompleteSearchRoomModel
import javax.inject.Inject

class AutoCompleteDaoRepository @Inject constructor(private val dao: AutoCompleteSearchDao) {
    suspend fun getAllSearchFiveResult(): List<AutoCompleteSearchRoomModel> = dao.getAllSearchResult()
    suspend fun addSearchResult(searchResult: AutoCompleteSearchRoomModel) {
        dao.addSearchResult(searchResult)
    }
}