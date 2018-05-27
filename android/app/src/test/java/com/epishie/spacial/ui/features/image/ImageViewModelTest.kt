package com.epishie.spacial.ui.features.image

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.epishie.spacial.model.ImageEntity
import com.epishie.spacial.model.ImageRepository
import com.epishie.spacial.model.NetworkError
import com.epishie.spacial.ui.features.common.TextProvider
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ImageViewModelTest {
    @Rule @JvmField
    val taskRule = InstantTaskExecutorRule()

    lateinit var vm: ImageViewModel
    @MockK
    lateinit var imageRepository: ImageRepository
    @MockK
    lateinit var textProvider: TextProvider
    val testImageEntity1 = ImageEntity("1",
            "http://image.com/1",
            null,
            "Image 1",
            "This is image 1")

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        vm = ImageViewModel(imageRepository, textProvider)

        every { textProvider.getErrorMessage(ofType(NetworkError::class)) } returns "Network error"
    }

    @Test
    fun loadError() {
        every { imageRepository.getImage("1") } returns Single.error(NetworkError())
        vm.load("1")
        vm.error.observeForever {  }
        vm.imageUrl.observeForever {  }
        vm.title.observeForever {  }
        vm.description.observeForever {  }

        assertThat(vm.error.value)
                .isEqualTo("Network error")
        assertThat(vm.imageUrl.value)
                .isNull()
        assertThat(vm.title.value)
                .isNull()
        assertThat(vm.description.value)
                .isNull()
    }

    @Test
    fun loadSuccess() {
        every { imageRepository.getImage("1") } returns Single.just(testImageEntity1)
        vm.load("1")
        vm.error.observeForever {  }
        vm.imageUrl.observeForever {  }
        vm.title.observeForever {  }
        vm.description.observeForever {  }

        assertThat(vm.error.value)
                .isNull()
        assertThat(vm.imageUrl.value)
                .isEqualTo("http://image.com/1")
        assertThat(vm.title.value)
                .isEqualTo("Image 1")
        assertThat(vm.description.value)
                .isEqualTo("This is image 1")
    }
}