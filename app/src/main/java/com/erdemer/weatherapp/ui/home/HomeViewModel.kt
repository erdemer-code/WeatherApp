package com.erdemer.weatherapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdemer.weatherapp.model.local.AutoCompleteSearchRoomModel
import com.erdemer.weatherapp.model.response.AutoCompleteSearchResponse
import com.erdemer.weatherapp.model.response.AutoCompleteSearchResponseItem
import com.erdemer.weatherapp.repository.AutoCompleteDaoRepository
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
class HomeViewModel @Inject constructor(
    private val autoCompleteSearchRepository: AutoCompleteSearchRepository,
    private val autoCompleteDaoRepository: AutoCompleteDaoRepository
) :
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
                        oldState.copy(
                            autoCompleteSearch = response.data,
                            typing = true,
                            currentState = State.AUTOCOMPLETE_SEARCH_REMOTE
                        )
                    }
                }
                is Resource.Error -> {
                    _event.send(Event.Error(response.message))
                }
            }
            _event.send(Event.Loading(isLoading = false))
        }
    }

    fun saveLocation(location: AutoCompleteSearchUiModel, clickTime: Long) {
        viewModelScope.launch {
            autoCompleteDaoRepository.addSearchResult(mapToRoomModel(location, clickTime))
            _state.update { oldState -> oldState.copy(typing = true, currentState = State.AUTOCOMPLETE_SEARCH_LOCAL) }
        }
    }

    fun returnAutoCompleteDbResult() {
        viewModelScope.launch {
            _event.send(Event.Loading(true))
            _state.update { oldState ->
                oldState.copy(
                    localAutoCompleteSearch = autoCompleteDaoRepository.getAllSearchFiveResult()
                        .map {
                            mapToAutoCompleteUiModel(it)
                        }, typing = false, currentState = State.AUTOCOMPLETE_SEARCH_LOCAL
                )
            }

            _event.send(Event.Loading(false))
        }
    }

    private fun mapToRoomModel(
        location: AutoCompleteSearchUiModel,
        clickTime: Long
    ): AutoCompleteSearchRoomModel =
        AutoCompleteSearchRoomModel(
            cityName = location.cityName,
            countryName = location.countryName,
            cityId = location.cityId,
            countryId = location.countryId,
            timeStamp = clickTime,
            key = location.key
        )

    fun mapToAutoCompleteUiModel(response: AutoCompleteSearchResponseItem?) =
        AutoCompleteSearchUiModel(
            cityId = response?.administrativeArea?.id,
            cityName = response?.administrativeArea?.localizedName,
            countryName = response?.country?.localizedName,
            countryId = response?.country?.id,
            key = response?.key,
        )

    private fun mapToAutoCompleteUiModel(response: AutoCompleteSearchRoomModel?) =
        AutoCompleteSearchUiModel(
            cityId = response?.cityId,
            cityName = response?.cityName,
            countryName = response?.countryName,
            countryId = response?.countryId,
            key = response?.key,
        )

    sealed interface Event {
        data class Loading(val isLoading: Boolean) : Event
        data class Error(val error: String?) : Event
    }

    enum class State {
        INITIAL,
        AUTOCOMPLETE_SEARCH_REMOTE,
        AUTOCOMPLETE_SEARCH_LOCAL,
    }

    data class UiState(
        val autoCompleteSearch: AutoCompleteSearchResponse? = null,
        val localAutoCompleteSearch: List<AutoCompleteSearchUiModel>? = null,
        val typing: Boolean? = null,
        val currentState: State = State.INITIAL
    )
}