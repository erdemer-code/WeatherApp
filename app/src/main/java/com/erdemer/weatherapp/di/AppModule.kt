package com.erdemer.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.erdemer.weatherapp.BuildConfig
import com.erdemer.weatherapp.data.local.AppDatabase
import com.erdemer.weatherapp.data.local.AutoCompleteSearchDao
import com.erdemer.weatherapp.data.remote.factory.AutoCompleteSearchFactory
import com.erdemer.weatherapp.repository.AutoCompleteSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient
            .Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideAutoCompleteSearchDao(appDatabase: AppDatabase): AutoCompleteSearchDao =
        appDatabase.autoCompleteSearchDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "weather_app.db")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideAutoCompleteRepository(autoCompleteSearchFactory: AutoCompleteSearchFactory) =
        AutoCompleteSearchRepository(autoCompleteSearchFactory)

}