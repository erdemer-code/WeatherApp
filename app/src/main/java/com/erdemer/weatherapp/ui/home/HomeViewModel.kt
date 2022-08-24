package com.erdemer.weatherapp.ui.home

import androidx.lifecycle.ViewModel
import com.erdemer.weatherapp.repository.AutoCompleteSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val autoCompleteSearchRepository: AutoCompleteSearchRepository): ViewModel() {

}