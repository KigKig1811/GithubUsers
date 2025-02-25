package com.xt.githubusers.presentation.users

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xt.githubusers.R
import com.xt.githubusers.databinding.FragmentUsersBinding
import com.xt.githubusers.presentation.MainActivity
import com.xt.githubusers.presentation.users.adapter.LoadStateAdapter
import com.xt.githubusers.presentation.users.adapter.UserAdapter
import com.xt.githubusers.presentation.users.viewholder.dpToPx
import com.xt.githubusers.utils.BaseFragment
import com.xt.githubusers.utils.VerticalSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsersFragment : BaseFragment<FragmentUsersBinding>(FragmentUsersBinding::inflate) {

    override val viewModel: UsersViewModel by viewModels()

    private val usersAdapter: UserAdapter by lazy {
        UserAdapter { selectedUser ->
            findNavController().navigate(
                UsersFragmentDirections.actionUsersFragmentToUserDetailsFragment(selectedUser)
            )
        }
    }

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        initUi()
        initObserver()
    }

    private fun initUi() {
        (activity as? MainActivity)?.setToolbarTitle(title = getString(R.string.github_users))
        binding.rcvUsers.apply {
            layoutManager = LinearLayoutManager(context)
            val footerAdapter = LoadStateAdapter { usersAdapter.retry() }
            adapter = usersAdapter.withLoadStateFooter(footer = footerAdapter)
            val itemDecoration =
                VerticalSpaceItemDecoration(
                    context.dpToPx(8),
                    true
                )
            addItemDecoration(itemDecoration)
        }

    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.users.collectLatest { data ->
                        usersAdapter.submitData(lifecycle, data)
                    }
                }
            }
        }
    }
}