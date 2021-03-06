package com.epishie.spacial.ui.features.discover

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.PageKeyedDataSource
import com.epishie.spacial.model.*
import com.epishie.spacial.test.waitForValue
import com.epishie.spacial.ui.features.common.TextProvider
import com.epishie.spacial.ui.features.adapter.Thumbnail
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.Completable
import io.reactivex.Flowable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DiscoverViewModelTest {
    @Rule @JvmField
    val taskRule = InstantTaskExecutorRule()

    lateinit var vm: DiscoverViewModel
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
    val testCatalogEntity1 = CatalogEntity("sample",
            "http://thumbnail.com/1")

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        vm = DiscoverViewModel(imageRepository, catalogRepository, textProvider)

        every { textProvider.getErrorMessage(ofType(NetworkError::class)) } returns "Network Error"
        every { catalogRepository.getCatalog("sample") } returns Flowable.empty()
    }

    @Test
    fun searchNetworkError() {
        val factory: DataSource.Factory<String, ImageEntity> = mockk(relaxed = true)
        every { imageRepository.search("sample") } returns SearchResult(factory,
                MutableLiveData<Status>().apply {
                    value = Status.Error(NetworkError())
                }, {})
        vm.search("sample")
        vm.loaded.observeForever {  }
        vm.error.observeForever {  }
        vm.empty.observeForever {  }
        vm.savable.observeForever {  }

        assertThat(vm.loaded.value)
                .isEqualTo(false)
        assertThat(vm.error.value)
                .isEqualTo("Network Error")
        assertThat(vm.empty.value)
                .isEqualTo(false)
        assertThat(vm.savable.value)
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
        vm.loaded.observeForever {  }
        vm.empty.observeForever {  }
        vm.error.observeForever {  }
        vm.savable.observeForever {  }

        assertThat(vm.loaded.value)
                .isEqualTo(true)
        assertThat(vm.empty.value)
                .isEqualTo(true)
        assertThat(vm.error.value)
                .isNull()
        assertThat(vm.savable.value)
                .isEqualTo(false)
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
        vm.loaded.observeForever {  }
        vm.empty.observeForever {  }
        vm.error.observeForever {  }
        vm.savable.observeForever {  }

        assertThat(vm.thumbnails.value)
                .containsExactly(testThumbnail1, testThumbnail2)
        assertThat(vm.loaded.value)
                .isEqualTo(true)
        assertThat(vm.empty.value)
                .isEqualTo(false)
        assertThat(vm.error.value)
                .isNull()
        assertThat(vm.savable.value)
                .isEqualTo(true)
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
    fun save() {
        every { catalogRepository.addCatalog(testCatalogEntity1) } returns Completable.complete()
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
        vm.savable.observeForever {  }

        vm.save()

        verify { catalogRepository.addCatalog(testCatalogEntity1) }
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