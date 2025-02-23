package com.xt.githubusers.utils

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  [BaseViewModel] is an abstract base class for all ViewModels in the application.
 */
@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {
}