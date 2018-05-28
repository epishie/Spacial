package com.epishie.spacial.ui.features.main

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.paging.DataSource
import android.arch.paging.ItemKeyedDataSource
import com.epishie.spacial.model.CatalogEntity
import com.epishie.spacial.model.CatalogRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @Rule @JvmField
    val taskRule = InstantTaskExecutorRule()

    lateinit var vm: MainViewModel
    @MockK
    lateinit var catalogRepository: CatalogRepository
    val testCatalogEntity1 = CatalogEntity("Catalog 1",
            "http://catalog1.com")
    val testCatalogEntity2 = CatalogEntity("Catalog 2",
            "http://catalog2.com")
    val testCatalogEntity3 = CatalogEntity("Catalog 3",
            "http://catalog3.com")
    val testPreview1 = Preview("Catalog 1",
            "http://catalog1.com",
            "Catalog 1")
    val testPreview2 = Preview("Catalog 2",
            "http://catalog2.com",
            "Catalog 2")
    val testPreview3 = Preview("Catalog 3",
            "http://catalog3.com",
            "Catalog 3")

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun notEmpty() {
        val factory = object : DataSource.Factory<Int, CatalogEntity>() {
            override fun create(): DataSource<Int, CatalogEntity> {
                return MockDataSource(listOf(testCatalogEntity1,
                        testCatalogEntity2, testCatalogEntity3))
            }
        }
        every { catalogRepository.getCatalogs() } returns factory
        vm = MainViewModel(catalogRepository)
        vm.catalogs.observeForever {  }
        vm.empty.observeForever {  }

        assertThat(vm.catalogs.value)
                .containsExactly(testPreview1, testPreview2, testPreview3)
        assertThat(vm.empty.value)
                .isFalse()
    }

    @Test
    fun empty() {
        val factory = object : DataSource.Factory<Int, CatalogEntity>() {
            override fun create(): DataSource<Int, CatalogEntity> {
                return MockDataSource(emptyList())
            }
        }
        every { catalogRepository.getCatalogs() } returns factory
        vm = MainViewModel(catalogRepository)
        vm.catalogs.observeForever {  }
        vm.empty.observeForever {  }

        assertThat(vm.catalogs.value)
                .isEmpty()
        assertThat(vm.empty.value)
                .isTrue()
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