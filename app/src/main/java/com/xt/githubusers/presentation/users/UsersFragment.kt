package com.xt.githubusers.presentation.users

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xt.githubusers.databinding.FragmentUsersBinding
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
        UserAdapter()
    }

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        initUi()
        initObserver()
    }

    private fun initUi() {
        binding.rcvUsers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
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
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.usersState.collectLatest { state ->
                        when (state) {
                            is UiState.Loading -> {
//                                progressBar.visibility = View.VISIBLE
//                                recyclerView.visibility = View.GONE
//                                errorText.visibility = View.GONE
                            }

                            is UiState.Success -> {
                                usersAdapter.submitData(state.data)
//                                progressBar.visibility = View.GONE
//                                recyclerView.visibility = View.VISIBLE
//                                errorText.visibility = View.GONE
//                                adapter.submitData(lifecycle, state.data) // Cập nhật dữ liệu cho RecyclerView
                            }

                            is UiState.Error -> {
//                                progressBar.visibility = View.GONE
//                                recyclerView.visibility = View.GONE
//                                errorText.visibility = View.VISIBLE
//                                errorText.text = state.message
                            }
                        }
                    }
                }
            }
        }
    }
}