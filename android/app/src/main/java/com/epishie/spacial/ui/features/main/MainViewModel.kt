package com.epishie.spacial.ui.features.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations.map
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.epishie.spacial.model.CatalogRepository
import com.epishie.spacial.ui.PREVIEW_PAGE_SIZE
import javax.inject.Inject

class MainViewModel @Inject constructor(catalogRepository: CatalogRepository)
    : ViewModel() {
    val catalogs: LiveData<PagedList<Preview>> = LivePagedListBuilder(catalogRepository.getCatalogs()
            .map {
                Preview(it.name, it.coverUrl, it.name)
            }, PREVIEW_PAGE_SIZE)
            .build()

    val empty: LiveData<Boolean> = map(catalogs, { pagedList ->
        pagedList.isEmpty()
    })
}