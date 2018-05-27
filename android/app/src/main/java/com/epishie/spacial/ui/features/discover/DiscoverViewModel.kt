package com.epishie.spacial.ui.features.discover

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.map
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.epishie.spacial.model.ImageRepository
import com.epishie.spacial.model.Status
import com.epishie.spacial.ui.features.common.TextProvider
import com.epishie.spacial.ui.features.adapter.Thumbnail
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(imageRepository: ImageRepository,
                                            textProvider: TextProvider) : ViewModel() {
    private companion object {
        const val PAGE_SIZE = 20
    }

    private val searchQuery = MutableLiveData<String>()
    private val result = map(searchQuery, {
        imageRepository.search(it)
    })
    val thumbnails: LiveData<PagedList<Thumbnail>> = switchMap(result, { result ->
        LivePagedListBuilder(result.dataSourceFactory.map {
            Thumbnail(it.id, it.thumbnailUrl
                    ?: "", it.title)
        },PAGE_SIZE).build()
    })
    val error: LiveData<CharSequence?> = switchMap(result, { result ->
        map(result.status, {
            when (it) {
                is Status.Error -> textProvider.getErrorMessage(it.throwable)
                else -> null
            }
        })
    })
    val empty: LiveData<Boolean> = switchMap(result, { result ->
        map(result.status, {
            when (it) {
                is Status.Loaded -> it.empty
                else -> false
            }
        })
    })
    val loaded: LiveData<Boolean> = switchMap(result, { result ->
        map(result.status, {
            it is Status.Loaded
        })
    })
    val savable: LiveData<Boolean> = switchMap(result, { result ->
        map(result.status, {
            it is Status.Loaded && !it.empty
        })
    })

    fun search(query: String) {
        searchQuery.value = query
    }

    fun retry() {
        result.value?.retry?.invoke()
    }

    override fun onCleared() {
        super.onCleared()
    }
}