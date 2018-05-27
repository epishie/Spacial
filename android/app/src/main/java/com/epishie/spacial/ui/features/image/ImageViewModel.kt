package com.epishie.spacial.ui.features.image

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.map
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import com.epishie.spacial.model.ImageEntity
import com.epishie.spacial.model.ImageRepository
import com.epishie.spacial.ui.extensions.toLiveData
import com.epishie.spacial.ui.features.common.TextProvider
import javax.inject.Inject

class ImageViewModel @Inject constructor(private val imageRepository: ImageRepository,
                                         private val textProvider: TextProvider)
    : ViewModel() {
    private val imageId = MutableLiveData<String>()
    private val result: LiveData<Result> = switchMap(imageId, {
        imageRepository.getImage(it)
                .map { Result(imageEntity = it) }
                .onErrorReturn { Result(error = it) }
                .toLiveData()
    })
    val error: LiveData<CharSequence?> = map(result, { result ->
        result.error?.let {
            textProvider.getErrorMessage(it)
        }
    })
    val imageUrl: LiveData<String> = map(result, { result ->
        result.imageEntity?.imageUrl
    })
    val title: LiveData<String> = map(result, { result ->
        result.imageEntity?.title
    })
    val description: LiveData<String> = map(result, { result ->
        result.imageEntity?.description
    })

    fun load(imageId: String) {
        this.imageId.value = imageId
    }

    data class Result(val imageEntity: ImageEntity? = null, val error: Throwable? = null)
}