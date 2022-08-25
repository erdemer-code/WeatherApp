package com.erdemer.weatherapp.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.erdemer.weatherapp.R
import com.erdemer.weatherapp.databinding.FragmentHomeBinding
import com.erdemer.weatherapp.model.response.AutoCompleteSearchResponse
import com.erdemer.weatherapp.ui.home.adapter.AutoCompleteSearchAdapter
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
    private var adapter: AutoCompleteSearchAdapter? = null

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
        initListeners()
        collectLatestFlow(viewModel.state, stateCollector)
        collectFlow(viewModel.event, eventCollector)
    }

    private fun initListeners() {

        binding.ivCloseSearch.setOnClickListener {
            binding.searchViewHome.setText("")
            binding.searchViewHome.clearFocus()
        }
        binding.searchViewHome.setOnFocusChangeListener { _, b ->
            if(b){
                viewModel.returnAutoCompleteDbResult()
            }
        }
        binding.searchViewHome.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)= Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty().not()){
                    binding.ivCloseSearch.visible()
                }
                else if ((p0?.length ?: 0) >= 3) {
                    binding.rvAutoCompleteSearch.visible()
                    viewModel.getAutoCompleteSearchResult(p0.toString())
                } else {
                    binding.ivCloseSearch.gone()
                }
            }

            override fun afterTextChanged(p0: Editable?) = Unit

        })
    }

    private fun setAdapter() {
        adapter = AutoCompleteSearchAdapter(autoCompleteSearchAdapterClickListener)
        binding.rvAutoCompleteSearch.adapter = adapter
    }

    private fun setSearchFragment() {
/*        binding.searchViewHome.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if ((newText?.length ?: 0) >= 3) {
                    //New Searches
                    binding.rvAutoCompleteSearch.visible()
                    viewModel.getAutoCompleteSearchResult(newText.toString())
                } else if (newText.isNullOrEmpty().not()) {
                    //Local (Old) Searches
                    viewModel.returnAutoCompleteDbResult()
                }
                return false
            }
        })*/
    }

    private val stateCollector: suspend (HomeViewModel.UiState) -> Unit = { state ->
        when(state.currentState){
            HomeViewModel.State.INITIAL -> onInitialState()
            HomeViewModel.State.AUTOCOMPLETE_SEARCH_LOCAL -> onLocalSearchResultArrived(state.localAutoCompleteSearch, state.typing ?: true)
            HomeViewModel.State.AUTOCOMPLETE_SEARCH_REMOTE -> onSearchResultArrived(state.autoCompleteSearch)
        }
    }

    private fun onInitialState() {
        setAdapter()
        setSearchFragment()
    }

    private fun onLocalSearchResultArrived(
        localAutoCompleteSearch: List<AutoCompleteSearchUiModel>?,
        isTyping: Boolean
    ) {
        Log.e("HomeFragment", "onLocalSearchResultArrived: ${localAutoCompleteSearch?.size}")
        if (localAutoCompleteSearch?.isNotEmpty() == true) {
            if (!isTyping) {
                binding.rvAutoCompleteSearch.visible()
                adapter?.submitList(localAutoCompleteSearch)
            } else {
                binding.rvAutoCompleteSearch.gone()
            }
        }
    }

    private val eventCollector: suspend (HomeViewModel.Event) -> Unit = { event ->
        when (event) {
            is HomeViewModel.Event.Loading -> setProgressBar(event.isLoading)
            is HomeViewModel.Event.Error -> Log.e("HomeFragment", event.error.toString())
        }
    }

    private fun setProgressBar(isLoading: Boolean) {
        if (isLoading)
            binding.progressBar.visible()
        else
            binding.progressBar.gone()
    }

    private fun onSearchResultArrived(autoCompleteSearch: AutoCompleteSearchResponse?) {
        adapter?.submitList(autoCompleteSearch?.map { viewModel.mapToAutoCompleteUiModel(it) })
    }

    private val autoCompleteSearchAdapterClickListener =
        fun(autoCompleteSearch: AutoCompleteSearchUiModel, _: Int) {
            Log.e("HomeFragment", "Clicked -> ${autoCompleteSearch.cityName}")
            viewModel.saveLocation(autoCompleteSearch, System.currentTimeMillis())
            binding.searchViewHome.setText("")
            binding.searchViewHome.clearFocus()
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


