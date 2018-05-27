package com.epishie.spacial.model

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.arch.paging.LivePagedListBuilder
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ImageRepositoryTest {
    @Rule @JvmField
    val taskRule = InstantTaskExecutorRule()

    lateinit var imageRepository: ImageRepository
    @MockK
    lateinit var imagesApi: ImageRepository.ImagesApi
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
    val testImageEntity3 = ImageEntity("3",
            "http://image.com",
            null,
            "Image 3",
            "This is image 3")
    val testNext = "next"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        imageRepository = ImageRepository(imagesApi)
    }

    @Test
    fun searchIOError() {
        every { imagesApi.search("sample") } returns Single.error(IOException())
        val result = imageRepository.search("sample")
        val statusObserver = spyk(Observer<Status> {
        })
        result.status.observeForever(statusObserver)

        LivePagedListBuilder(result.dataSourceFactory, 10)
                .build()
                .observeForever {  }

        verifySequence {
            statusObserver.onChanged(Status.Loading)
            statusObserver.onChanged(match {
                it is Status.Error && it.throwable is NetworkError
            })
        }
    }

    @Test
    fun searchError() {
        every { imagesApi.search("sample") } returns Single.error(IllegalStateException())
        val result = imageRepository.search("sample")
        val statusObserver = spyk(Observer<Status> {
        })
        result.status.observeForever(statusObserver)

        LivePagedListBuilder(result.dataSourceFactory, 10)
                .build()
                .observeForever {  }

        verifySequence {
            statusObserver.onChanged(Status.Loading)
            statusObserver.onChanged(match {
                it is Status.Error && it.throwable is IllegalStateException
            })
        }
    }

    @Test
    fun searchRetry() {
        every { imagesApi.search("sample") } returns Single.error(NetworkError())
        val result = imageRepository.search("sample")
        LivePagedListBuilder(result.dataSourceFactory, 10)
                .build()
                .observeForever {  }

        result.retry()

        verify(exactly = 2) { imagesApi.search("sample") }
    }

    @Test
    fun searchSuccess() {
        val page = ImageRepository.ImagesApi.Page(listOf(testImageEntity1, testImageEntity2),
                testNext)
        every { imagesApi.search("sample") } returns Single.just(page)
        val result = imageRepository.search("sample")
        val statusObserver = spyk(Observer<Status> { })
        result.status.observeForever(statusObserver)
        LivePagedListBuilder(result.dataSourceFactory, 10)
                .build()
                .observeForever {  }


        verifySequence {
            statusObserver.onChanged(Status.Loading)
            statusObserver.onChanged(Status.Loaded(false))
        }
    }

    @Test
    fun searchEmpty() {
        val page = ImageRepository.ImagesApi.Page(emptyList(), null)
        every { imagesApi.search("sample") } returns Single.just(page)
        val result = imageRepository.search("sample")
        val statusObserver = spyk(Observer<Status> {})
        result.status.observeForever(statusObserver)
        LivePagedListBuilder(result.dataSourceFactory, 10)
                .build()
                .observeForever {  }

        verifySequence {
            statusObserver.onChanged(Status.Loading)
            statusObserver.onChanged(Status.Loaded(true))
        }
    }

    @Test
    fun getImageSuccess() {
        every { imagesApi.getImage("3") } returns Single.just(testImageEntity3)
        val observer = imageRepository.getImage("3").test()

        assertThat(observer.values().toList())
                .containsExactly(testImageEntity3)
        assertThat(observer.errors().toList())
                .isEmpty()
    }

    @Test
    fun getImageNetworkError() {
        every { imagesApi.getImage("3") } returns Single.error(IOException())
        val observer = imageRepository.getImage("3").test()

        assertThat(observer.values().toList())
                .isEmpty()
        assertThat(observer.errors().toList())
                .hasOnlyElementsOfTypes(NetworkError::class.java)
    }
}