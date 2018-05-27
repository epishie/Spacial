package com.epishie.spacial.model.api

import com.epishie.spacial.model.ImageEntity
import com.epishie.spacial.model.ImageRepository
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class NasaApi : ImageRepository.ImagesApi {
    interface Api {
        @GET("/search?media_type=image")
        fun search(@Query("q") query: String? = null,
                   @Query("nasa_id") nasaId: String? = null)
                : Single<Response>
        @GET("/asset/{nasa_id}")
        fun asset(@Path("nasa_id") id: String): Single<Response>
    }

    private val api: Api = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://images-api.nasa.gov")
                    .build()
                    .create(Api::class.java)

    override fun search(query: String): Single<ImageRepository.ImagesApi.Page> {
        return api.search(query = query).map { response ->
            val thumbnails = response.collection.items.map(this::mapItem)
            ImageRepository.ImagesApi.Page(thumbnails, response.collection.links?.first()?.href)
        }
    }

    override fun getImage(id: String): Single<ImageEntity> {
        return Single.zip(api.search(nasaId = id), api.asset(id),
                BiFunction { searchResponse, assetResponse ->
                    val imageEntity = searchResponse.run {
                        mapItem(collection.items.first())
                    }
                    val image = assetResponse.run {
                        collection.items.find {
                            it.href?.contains("large") ?: false
                        }?.href
                    }
                    imageEntity.copy(imageUrl = image)
                })
    }

    private fun mapItem(item: Item): ImageEntity {
        val data = item.data.first()
        val link = item.links.first()
        return ImageEntity(data.nasa_id,
                null,
                link.href,
                data.title,
                data.description)
    }

    data class Response(val collection: Collection)
    data class Collection(val items: List<Item>, val links: List<Link>?)
    data class Item(val data: List<Data>, val links: List<Link>, val href: String?)
    data class Data(val nasa_id: String,
                    val title: String,
                    val description: String)
    data class Link(val href: String)
}