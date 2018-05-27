package com.epishie.spacial.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.switchMap
import android.arch.paging.DataSource
import android.arch.paging.PageKeyedDataSource
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ImageRepository @Inject constructor(private val imagesApi: ImagesApi) {
    fun search(query: String): SearchResult {
        val factory = ImageDataSourceFactory(imagesApi, query)
        return SearchResult(factory,
                switchMap(factory.dataSource, {
                    it.status
                }),
                {
                    factory.dataSource.value?.retry()
                })
    }

    interface ImagesApi {
        fun search(query: String): Single<Page>

        data class Page(val images: List<ImageEntity>, val next: String?)
    }

    private class ImageDataSource(private val imagesApi: ImagesApi,
                                  private val query: String)
        : PageKeyedDataSource<String, ImageEntity>() {
        val status = MutableLiveData<Status>()
        private var disposable: Disposable? = null
        private var retryRequest: (() -> Unit)? = null

        override fun loadInitial(params: LoadInitialParams<String>,
                                 callback: LoadInitialCallback<String, ImageEntity>) {
            disposable?.dispose()
            disposable = imagesApi.search(query)
                    .doOnSubscribe {
                        status.postValue(Status.Loading)
                    }
                    .subscribe { page, error ->
                        if (page != null) {
                            callback.onResult(page.images, null, page.next)
                            status.postValue(Status.Loaded(page.images.isEmpty()))
                            retryRequest = null
                        }
                        if (error != null) {
                            retryRequest = {
                                loadInitial(params, callback)
                            }
                            status.postValue(Status.Error(NetworkError()))
                        }
                    }
        }

        override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, ImageEntity>) {
            disposable?.dispose()
            disposable = imagesApi.search(query)
                    .doOnSubscribe {
                        status.postValue(Status.Loading)
                    }
                    .subscribe { page, error ->
                        if (page != null) {
                            callback.onResult(page.images, page.next)
                            retryRequest = null
                        }
                        if (error != null) {
                            retryRequest = {
                                loadAfter(params, callback)
                            }
                            status.postValue(Status.Error(NetworkError()))
                        }
                    }
        }

        override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, ImageEntity>) {
            // not supported
        }

        fun retry() {
            val request = retryRequest
            retryRequest = null
            request?.invoke()
        }
    }

    private class ImageDataSourceFactory(private val imagesApi: ImagesApi,
                                         private val query: String)
        : DataSource.Factory<String, ImageEntity>() {
        val dataSource = MutableLiveData<ImageDataSource>()

        override fun create(): DataSource<String, ImageEntity> {
            val dataSource = ImageDataSource(imagesApi, query)
            this.dataSource.postValue(dataSource)
            return dataSource
        }
    }
}