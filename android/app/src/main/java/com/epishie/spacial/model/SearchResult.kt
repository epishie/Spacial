package com.epishie.spacial.model

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource

data class SearchResult(val dataSourceFactory: DataSource.Factory<String, ImageEntity>,
                        val status: LiveData<Status>,
                        val retry: () -> Unit)