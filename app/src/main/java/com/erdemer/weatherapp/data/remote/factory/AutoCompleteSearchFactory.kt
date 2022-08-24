package com.erdemer.weatherapp.data.remote.factory

import com.erdemer.weatherapp.BuildConfig
import com.erdemer.weatherapp.model.response.AutoCompleteSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AutoCompleteSearchFactory {
    @GET("locations/v1/cities/autocomplete")
    suspend fun getAutoCompleteSearch(
        @Query("apikey") apikey: String = BuildConfig.API_KEY,
        @Query("q") q: String
    ): AutoCompleteSearchResponse
}