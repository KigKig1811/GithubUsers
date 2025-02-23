package com.xt.githubusers.presentation.users.viewholder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.xt.githubusers.R
import com.xt.githubusers.databinding.ItemUserBinding
import com.xt.githubusers.domain.model.UserModel

class UserViewHolder(
    private val binding: ItemUserBinding,
    private val onUserClick: (String) -> Unit = {}
) : RecyclerView.ViewHolder(binding.root) {

    private val context by lazy { binding.root.context }

    fun bind(user: UserModel) {
        binding.tvName.text = user.login
        binding.tvUrlProfile.text = user.htmlUrl
        Glide.with(context)
            .load(user.avatarUrl)
            .placeholder(R.drawable.ic_avatar_def)
            .error(R.drawable.ic_avatar_def)
            .apply(
                RequestOptions()
                    .transform(
                        CircleCrop()
                    )
            )
            .into(binding.ivAvatar)

        binding.root.setOnClickListener {
            val userName = user.login ?: return@setOnClickListener
            onUserClick.invoke(userName)
        }
    }
}

fun Context.dpToPx(dp: Int): Int {
    return (dp * this.resources.displayMetrics.density).toInt()
}