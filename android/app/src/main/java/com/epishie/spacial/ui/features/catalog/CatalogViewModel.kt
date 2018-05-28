package com.epishie.spacial.ui.features.catalog

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.map
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.epishie.spacial.model.CatalogRepository
import com.epishie.spacial.model.ImageRepository
import com.epishie.spacial.model.Status
import com.epishie.spacial.ui.PAGE_SIZE
import com.epishie.spacial.ui.features.adapter.Thumbnail
import com.epishie.spacial.ui.features.common.TextProvider
import javax.inject.Inject

class CatalogViewModel @Inject constructor(imageRepository: ImageRepository,
                                           private val catalogRepository: CatalogRepository,
                                           textProvider: TextProvider)
    : ViewModel() {
    private val searchQuery = MutableLiveData<String>()
    private val searchResult = map(searchQuery, {
        imageRepository.search(it)
    })
    private val searchStatus = switchMap(searchResult, {
        it?.status
    })
    val thumbnails: LiveData<PagedList<Thumbnail>> = switchMap(searchResult, { result ->
        LivePagedListBuilder(result.dataSourceFactory.map {
            Thumbnail(it.id, it.thumbnailUrl
                    ?: "", it.title)
        }, PAGE_SIZE).build()
    })
    val error: LiveData<CharSequence?> = switchMap(searchResult, { result ->
        map(result.status, {
            when (it) {
                is Status.Error -> textProvider.getErrorMessage(it.throwable)
                else -> null
            }
        })
    })
    val empty: LiveData<Boolean> = map(searchStatus, { status ->
        when (status) {
            is Status.Loaded -> status.empty
            else -> false
        }
    })
    val loaded: LiveData<Boolean> = map(searchStatus, { status ->
        status is Status.Loaded
    })
    val loading: LiveData<Boolean> = map(searchStatus, { status ->
        status === Status.Loading
    })

    fun search(query: String) {
        if (searchQuery.value != query) {
            searchQuery.value = query
        }
    }

    fun retry() {
        searchResult.value?.retry?.invoke()
    }

    fun delete() {
        searchQuery.value?.let {
            catalogRepository.deleteCatalog(it)
                    .subscribe()
        }
    }
}