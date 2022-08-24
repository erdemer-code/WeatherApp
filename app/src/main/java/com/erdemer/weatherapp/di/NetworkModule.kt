package com.erdemer.weatherapp.di

import com.erdemer.weatherapp.data.remote.factory.AutoCompleteSearchFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideAutoCompleteSearchFactory(retrofit: Retrofit) = retrofit.create(AutoCompleteSearchFactory::class.java)


}