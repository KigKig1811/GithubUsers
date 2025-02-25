package com.xt.githubusers.domain.model

import androidx.recyclerview.widget.DiffUtil

data class UserModel(
    val id: Int? = null,
    val login: String? = null,
//    val nodeId: String? = null,
//    val type: String? = null,
//    val siteAdmin: Boolean? = null,
    val avatarUrl: String? = null,
    val htmlUrl: String? = null,
//    val url: String? = null,
//    val gravatarId: String? = null,
//    val eventsUrl: String? = null,
//    val followersUrl: String? = null,
//    val followingUrl: String? = null,
//    val gistsUrl: String? = null,
//    val organizationsUrl: String? = null,
//    val receivedEventsUrl: String? = null,
//    val reposUrl: String? = null,
//    val starredUrl: String? = null,
//    val subscriptionsUrl: String? = null,
//    val userViewType: String? = null,
    val followers: Int? = null,
    val following: Int? = null,
    val location: String? = null,
) {
    companion object {
        val DIFF_UTIL_ITEM_CALLBACK = object : DiffUtil.ItemCallback<UserModel>() {
            override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return oldItem == newItem
            }
        }


    }
}