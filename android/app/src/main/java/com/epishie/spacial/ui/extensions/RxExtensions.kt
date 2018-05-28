package com.epishie.spacial.ui.extensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import io.reactivex.Flowable
import io.reactivex.Single

// Remove once KTX is available
fun <T> Single<T>.toLiveData(): LiveData<T> {
    return this.toFlowable().toLiveData()
}

fun <T> Flowable<T>.toLiveData(): LiveData<T> {
    return LiveDataReactiveStreams.fromPublisher(this)
}