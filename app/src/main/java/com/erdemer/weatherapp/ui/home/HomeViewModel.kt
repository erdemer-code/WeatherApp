package com.erdemer.weatherapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdemer.weatherapp.model.response.AutoCompleteSearchResponse
import com.erdemer.weatherapp.repository.AutoCompleteSearchRepository
import com.erdemer.weatherapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val autoCompleteSearchRepository: AutoCompleteSearchRepository) :
    ViewModel() {
    private val _event = Channel<Event>(Channel.UNLIMITED)
    val event = _event.consumeAsFlow()

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun getAutoCompleteSearchResult(query: String) {
        viewModelScope.launch {
            _event.send(Event.Loading(isLoading = true))
            when (val response = autoCompleteSearchRepository.getAutoCompleteSearch(query)) {
                is Resource.Success -> {
                    _state.update { oldState ->
                        oldState.copy(autoCompleteSearch = response.data)
                    }
                }
                is Resource.Error -> {
                    _event.send(Event.Error(response.message))
                }
            }
            _event.send(Event.Loading(isLoading = false))
        }
    }


    sealed interface Event {
        data class Loading(val isLoading: Boolean) : Event
        data class Error(val error: String?) : Event
    }

    data class UiState(
        val autoCompleteSearch: AutoCompleteSearchResponse? = null
    )
}