package com.erdemer.weatherapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erdemer.weatherapp.R
import com.erdemer.weatherapp.databinding.ItemAutoCompleteSearchBinding
import com.erdemer.weatherapp.ui.home.AutoCompleteSearchUiModel

class AutoCompleteSearchAdapter(private val onClickListener: (AutoCompleteSearchUiModel, Int) -> Unit) :
    ListAdapter<AutoCompleteSearchUiModel, AutoCompleteSearchAdapter.AutoCompleteSearchViewHolder>(
        DIFF_CALLBACK
    ) {
    inner class AutoCompleteSearchViewHolder(val binding: ItemAutoCompleteSearchBinding) :
        RecyclerView.ViewHolder(binding.root)


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AutoCompleteSearchUiModel>() {
            override fun areItemsTheSame(
                oldItem: AutoCompleteSearchUiModel,
                newItem: AutoCompleteSearchUiModel
            ): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(
                oldItem: AutoCompleteSearchUiModel,
                newItem: AutoCompleteSearchUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AutoCompleteSearchViewHolder {
        val binding: ItemAutoCompleteSearchBinding = ItemAutoCompleteSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AutoCompleteSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AutoCompleteSearchViewHolder, position: Int) {
        with(holder){
            with(getItem(position)){
                binding.tvAutoCompleteSearch.text =
                    itemView.context.getString(R.string.rv_auto_complete_item_text,this.cityName,this.countryId)
                binding.root.setOnClickListener {
                    onClickListener(this, position)
                }
            }
        }
    }
}
