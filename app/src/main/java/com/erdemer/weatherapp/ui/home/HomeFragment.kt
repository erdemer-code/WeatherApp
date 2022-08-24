package com.erdemer.weatherapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.erdemer.weatherapp.databinding.FragmentHomeBinding
import com.erdemer.weatherapp.model.response.AutoCompleteSearchResponse
import com.erdemer.weatherapp.util.extension.collectFlow
import com.erdemer.weatherapp.util.extension.collectLatestFlow
import com.erdemer.weatherapp.util.extension.gone
import com.erdemer.weatherapp.util.extension.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectLatestFlow(viewModel.state,stateCollector)
        collectFlow(viewModel.event,eventCollector)
        viewModel.getAutoCompleteSearchResult("Ist")
    }

    private val stateCollector: suspend (HomeViewModel.UiState) -> Unit = {state ->
        onSearchResult(state.autoCompleteSearch)
    }

    private val eventCollector: suspend (HomeViewModel.Event) -> Unit = {event ->
        when(event){
            is HomeViewModel.Event.Loading -> setProgressBar(event.isLoading)
            is HomeViewModel.Event.Error -> Log.e("HomeFragment",event.error.toString())
        }
    }

    private fun setProgressBar(isLoading: Boolean) {
        if (isLoading)
            binding.progressBar.visible()
        else
            binding.progressBar.gone()
    }

    private fun onSearchResult(autoCompleteSearch: AutoCompleteSearchResponse?) {
        Log.d("HomeFragment", "onSearchResult: $autoCompleteSearch")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}