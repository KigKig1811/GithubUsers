package com.xt.githubusers.presentation.users.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.xt.githubusers.databinding.ItemUserBinding
import com.xt.githubusers.domain.model.UserModel
import com.xt.githubusers.presentation.users.viewholder.UserViewHolder

class UserAdapter(
    private val onUserClick: (String) -> Unit = {}
) : PagingDataAdapter<UserModel, UserViewHolder>(UserModel.DIFF_UTIL_ITEM_CALLBACK) {

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position) ?: return
        holder.bind(user = user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding = binding, onUserClick = onUserClick)
    }
}
