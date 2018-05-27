package com.epishie.spacial.ui.features.common

import android.content.Context
import com.epishie.spacial.R
import com.epishie.spacial.model.NetworkError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextProvider @Inject constructor(private val context: Context) {
    fun getErrorMessage(throwable: Throwable): CharSequence {
        return when (throwable) {
            is NetworkError -> context.getString(R.string.msg_network_error)
            else -> context.getString(R.string.msg_unknown)
        }
    }
}