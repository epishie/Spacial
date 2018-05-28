package com.epishie.spacial.ui.features.catalog

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.epishie.spacial.model.CatalogRepository
import com.epishie.spacial.ui.PAGE_SIZE
import com.epishie.spacial.ui.features.adapter.Thumbnail
import javax.inject.Inject

class CatalogListViewModel @Inject constructor(catalogRepository: CatalogRepository) : ViewModel() {
    val catalogs: LiveData<PagedList<Thumbnail>> = LivePagedListBuilder(catalogRepository.getCatalogs()
            .map {
                Thumbnail(it.name, it.coverUrl, it.name)
            }, PAGE_SIZE)
            .build()
}