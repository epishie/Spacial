package com.epishie.spacial.ui.features.catalog

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.PageKeyedDataSource
import com.epishie.spacial.model.*
import com.epishie.spacial.test.waitForValue
import com.epishie.spacial.ui.features.adapter.Thumbnail
import com.epishie.spacial.ui.features.common.TextProvider
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CatalogViewModelTest {
    @Rule @JvmField
    val taskRule = InstantTaskExecutorRule()
    lateinit var vm: CatalogViewModel
    @MockK
    lateinit var imageRepository: ImageRepository
    @MockK
    lateinit var catalogRepository: CatalogRepository
    @MockK
    lateinit var textProvider: TextProvider
    val testImageEntity1 = ImageEntity("1",
            null,
            "http://thumbnail.com/1",
            "Image 1",
            "This is image 1")
    val testImageEntity2 = ImageEntity("2",
            null,
            "http://thumbnail.com/2",
            "Image 2",
            "This is image 2")
    val testThumbnail1 = Thumbnail("1",
            "http://thumbnail.com/1",
            "Image 1")
    val testThumbnail2 = Thumbnail("2",
            "http://thumbnail.com/2",
            "Image 2")

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        vm = CatalogViewModel(imageRepository, catalogRepository, textProvider)

        every { catalogRepository.deleteCatalog(any()) } returns Completable.complete()
        every { textProvider.getErrorMessage(ofType(NetworkError::class)) } returns "Network Error"
    }

    @Test
    fun searchNetworkError() {
        val factory: DataSource.Factory<String, ImageEntity> = mockk(relaxed = true)
        every { imageRepository.search("sample") } returns SearchResult(factory,
                MutableLiveData<Status>().apply {
                    value = Status.Error(NetworkError())
                }, {})
        vm.search("sample")
        vm.error.observeForever {  }
        vm.empty.observeForever {  }

        assertThat(vm.error.value)
                .isEqualTo("Network Error")
        assertThat(vm.empty.value)
                .isEqualTo(false)
    }

    @Test
    fun searchEmpty() {
        val factory: DataSource.Factory<String, ImageEntity> = mockk(relaxed = true)
        every { imageRepository.search("sample") } returns SearchResult(factory,
                MutableLiveData<Status>().apply {
                    value = Status.Loaded(true)
                }, {})
        vm.search("sample")
        vm.empty.observeForever {  }
        vm.error.observeForever {  }

        assertThat(vm.empty.value)
                .isEqualTo(true)
        assertThat(vm.error.value)
                .isNull()
    }

    @Test
    fun searchSuccess() {
        val factory = object : DataSource.Factory<String, ImageEntity>() {
            override fun create(): DataSource<String, ImageEntity> {
                return MockDataSource(listOf(testImageEntity1, testImageEntity2))
            }
        }
        every { imageRepository.search("sample") } returns SearchResult(factory,
                MutableLiveData<Status>().apply {
                    value = Status.Loaded(false)
                }, {})
        vm.search("sample")
        vm.thumbnails.observeForever {  }
        vm.empty.observeForever {  }
        vm.error.observeForever {  }

        assertThat(vm.thumbnails.value)
                .containsExactly(testThumbnail1, testThumbnail2)
        assertThat(vm.empty.value)
                .isEqualTo(false)
        assertThat(vm.error.value)
                .isNull()
    }

    @Test
    fun retry() {
        val factory: DataSource.Factory<String, ImageEntity> = mockk(relaxed = true)
        val retry = spyk({})
        every { imageRepository.search("sample") } returns SearchResult(factory,
                MutableLiveData<Status>().apply {
                    value = Status.Loaded(true)
                }, retry)
        vm.search("sample")
        vm.empty.waitForValue()
        vm.retry()

        verify { retry() }
    }

    @Test
    fun delete() {
        val factory = object : DataSource.Factory<String, ImageEntity>() {
            override fun create(): DataSource<String, ImageEntity> {
                return MockDataSource(listOf(testImageEntity1, testImageEntity2))
            }
        }
        every { imageRepository.search("sample") } returns SearchResult(factory,
                MutableLiveData<Status>().apply {
                    value = Status.Loaded(false)
                }, {})
        vm.search("sample")
        vm.thumbnails.observeForever {  }
        vm.empty.observeForever {  }
        vm.error.observeForever {  }

        vm.delete()

        verify { catalogRepository.deleteCatalog("sample") }
    }

    private class MockDataSource(val value: List<ImageEntity>) : PageKeyedDataSource<String, ImageEntity>() {
        override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, ImageEntity>) {
            callback.onResult(value, null, null)
        }

        override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, ImageEntity>) {
        }

        override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, ImageEntity>) { }
    }
}