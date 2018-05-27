package com.epishie.spacial.ui.features.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Lazy
import javax.inject.Inject

class ViewModelFactory<T> @Inject constructor(private val vm: Lazy<T>)
    : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return vm.get() as T
    }
}
