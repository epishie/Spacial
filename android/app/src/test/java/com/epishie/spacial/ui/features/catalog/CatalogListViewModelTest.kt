package com.epishie.spacial.ui.features.catalog

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.paging.DataSource
import android.arch.paging.ItemKeyedDataSource
import com.epishie.spacial.model.CatalogEntity
import com.epishie.spacial.model.CatalogRepository
import com.epishie.spacial.ui.features.adapter.Thumbnail
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CatalogListViewModelTest {
    @Rule
    @JvmField
    val taskRule = InstantTaskExecutorRule()

    lateinit var vm: CatalogListViewModel
    @MockK
    lateinit var catalogRepository: CatalogRepository
    val testCatalogEntity1 = CatalogEntity("Catalog 1",
            "http://catalog1.com")
    val testCatalogEntity2 = CatalogEntity("Catalog 2",
            "http://catalog2.com")
    val testCatalogEntity3 = CatalogEntity("Catalog 3",
            "http://catalog3.com")
    val testThumbnail1 = Thumbnail("Catalog 1",
            "http://catalog1.com",
            "Catalog 1")
    val testThumbnail2 = Thumbnail("Catalog 2",
            "http://catalog2.com",
            "Catalog 2")
    val testThumbnail3 = Thumbnail("Catalog 3",
            "http://catalog3.com",
            "Catalog 3")

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }
    
    @Test
    fun initialize() {
        val factory = object : DataSource.Factory<Int, CatalogEntity>() {
            override fun create(): DataSource<Int, CatalogEntity> {
                return MockDataSource(listOf(testCatalogEntity1,
                        testCatalogEntity2, testCatalogEntity3))
            }
        }
        every { catalogRepository.getCatalogs() } returns factory
        vm = CatalogListViewModel(catalogRepository)
        vm.catalogs.observeForever {  }

        assertThat(vm.catalogs.value)
                .containsExactly(testThumbnail1, testThumbnail2, testThumbnail3)
    }
    
    private class MockDataSource(val value: List<CatalogEntity>) : ItemKeyedDataSource<Int, CatalogEntity>() {
        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<CatalogEntity>) {
            callback.onResult(value)
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<CatalogEntity>) { }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<CatalogEntity>) { }

        override fun getKey(item: CatalogEntity): Int {
            return value.indexOf(item)
        }
    }
}