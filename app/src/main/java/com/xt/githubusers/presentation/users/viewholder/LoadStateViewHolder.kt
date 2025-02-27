package com.xt.githubusers.presentation.users.viewholder

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.xt.githubusers.databinding.ItemLoadStateBinding

class LoadStateViewHolder(
    private val binding: ItemLoadStateBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnRetry.setOnClickListener { retry() }
    }

    fun bind(loadState: LoadState) {
        val isLoading = loadState is LoadState.Loading
        val isError = loadState is LoadState.Error

        binding.progressBar.isVisible = isLoading
        binding.btnRetry.isVisible = isError
    }

}
