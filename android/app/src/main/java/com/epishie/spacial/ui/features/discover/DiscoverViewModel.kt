package com.epishie.spacial.ui.features.discover

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.map
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.epishie.spacial.model.CatalogEntity
import com.epishie.spacial.model.CatalogRepository
import com.epishie.spacial.model.ImageRepository
import com.epishie.spacial.model.Status
import com.epishie.spacial.ui.extensions.toLiveData
import com.epishie.spacial.ui.features.common.TextProvider
import com.epishie.spacial.ui.features.adapter.Thumbnail
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(imageRepository: ImageRepository,
                                            private val catalogRepository: CatalogRepository,
                                            textProvider: TextProvider) : ViewModel() {
    private companion object {
        const val PAGE_SIZE = 20
    }

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
        },PAGE_SIZE).build()
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
    private val saved: LiveData<Boolean> = switchMap(searchQuery, {
        catalogRepository.getCatalog(it)
                .map { !it.isEmpty() }
                .onErrorReturnItem(false)
                .toLiveData()
    })

    val savable = MediatorLiveData<Boolean>().apply {
        val update = { _: Any? ->
            val hasResults = searchStatus.value?.let {
                it is Status.Loaded && !it.empty
            } ?: false
            val savedValue = saved.value ?: false
            postValue(hasResults && !savedValue)
        }
        addSource(searchStatus, update)
        addSource(saved, update)
    }

    fun search(query: String) {
        searchQuery.value = query
    }

    fun retry() {
        searchResult.value?.retry?.invoke()
    }

    fun save() {
        val name = searchQuery.value ?: return
        val thumbnail = thumbnails.value?.first() ?: return
        catalogRepository.addCatalog(CatalogEntity(name, thumbnail.url))
                .subscribe()
    }
}