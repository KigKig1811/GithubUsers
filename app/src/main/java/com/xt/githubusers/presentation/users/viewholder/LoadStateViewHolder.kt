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
        binding.apply {
            progressBar.isVisible = loadState is LoadState.Loading
            tvErrorMessage.isVisible = loadState is LoadState.Error
            btnRetry.isVisible = loadState is LoadState.Error

            if (loadState is LoadState.Error) {
                tvErrorMessage.text = loadState.error.localizedMessage
            }
        }
    }
}