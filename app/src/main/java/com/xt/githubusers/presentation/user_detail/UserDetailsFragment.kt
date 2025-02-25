package com.xt.githubusers.presentation.user_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.xt.githubusers.R
import com.xt.githubusers.databinding.FragmentUserDetailBinding
import com.xt.githubusers.domain.model.UserModel
import com.xt.githubusers.presentation.MainActivity
import com.xt.githubusers.utils.BaseFragment
import com.xt.githubusers.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class UserDetailsFragment :
    BaseFragment<FragmentUserDetailBinding>(FragmentUserDetailBinding::inflate) {

    override val viewModel: UserDetailViewModel by viewModels()

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.setToolbarTitle(title = getString(R.string.users_details))
        initObserver()
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { result ->
                        binding.progressBar.isVisible = result is Result.Loading
                        binding.grView.isVisible = result is Result.Success
                        binding.tvError.isVisible = result is Result.Error

                        when (result) {
                            is Result.Success -> initUserInfo(result.data)

                            is Result.Error -> {
                                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                binding.tvError.text = result.message
                            }

                            Result.Loading -> Unit
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun initUserInfo(userModel: UserModel) {
        binding.apply {
            layoutInfo.tvName.text = userModel.login
            layoutInfo.tvUrlProfile.isVisible = false
            layoutInfo.tvLocation.isVisible = true
            layoutInfo.tvLocation.text = userModel.location
            tvFollower.text = getString(R.string.follower, userModel.followers)
            tvFollowing.text = getString(R.string.following, userModel.following)
            tvBlog.text = userModel.htmlUrl

            val context = context ?: return
            Glide.with(context)
                .load(userModel.avatarUrl)
                .placeholder(R.drawable.ic_avatar_def)
                .error(R.drawable.ic_avatar_def)
                .apply(
                    RequestOptions()
                        .transform(
                            CircleCrop()
                        )
                )
                .into(layoutInfo.ivAvatar)
        }
    }
}