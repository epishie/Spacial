package com.epishie.spacial.test

import android.arch.lifecycle.LiveData
import java.util.concurrent.CountDownLatch

fun <T> LiveData<T>.waitForValue() {
    val latch = CountDownLatch(1)
    this.observeForever {
        latch.countDown()
    }
    latch.await()
}
