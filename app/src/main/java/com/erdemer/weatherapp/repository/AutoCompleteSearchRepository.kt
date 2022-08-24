package com.erdemer.weatherapp.repository

import com.erdemer.weatherapp.data.remote.factory.AutoCompleteSearchFactory
import com.erdemer.weatherapp.model.response.AutoCompleteSearchResponse
import com.erdemer.weatherapp.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AutoCompleteSearchRepository @Inject constructor(private val autoCompleteSearchFactory: AutoCompleteSearchFactory) {
    suspend fun getAutoCompleteSearch(query: String): Resource<AutoCompleteSearchResponse> {
        val response = try {
            autoCompleteSearchFactory.getAutoCompleteSearch(q = query)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Error")
        }
        return Resource.Success(response)
    }
}