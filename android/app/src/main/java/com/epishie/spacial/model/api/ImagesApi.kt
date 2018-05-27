package com.epishie.spacial.model.api

import com.epishie.spacial.model.ImageEntity
import com.epishie.spacial.model.ImageRepository
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class NasaApi : ImageRepository.ImagesApi {
    interface Api {
        @GET("/search?media_type=image")
        fun search(@Query("q") query: String): Single<SearchResponse>
    }

    private val api: Api = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://images-api.nasa.gov")
                    .build()
                    .create(Api::class.java)

    override fun search(query: String): Single<ImageRepository.ImagesApi.Page> {
        return api.search(query).map { response ->
            val thumbnails = response.collection.items.map {  item ->
                val data = item.data.first()
                val link = item.links.first()
                ImageEntity(data.nasa_id,
                        null,
                        link.href,
                        data.title,
                        data.description)
            }
            ImageRepository.ImagesApi.Page(thumbnails, response.collection.links?.first()?.href)
        }
    }

    data class SearchResponse(val collection: Collection)
    data class Collection(val items: List<Item>, val links: List<Link>?)
    data class Item(val data: List<Data>, val links: List<Link>)
    data class Data(val nasa_id: String,
                    val title: String,
                    val description: String)
    data class Link(val href: String)
}