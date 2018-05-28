package com.epishie.spacial.model

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.paging.DataSource
import android.arch.paging.ItemKeyedDataSource
import android.arch.paging.LivePagedListBuilder
import android.database.sqlite.SQLiteException
import com.epishie.spacial.model.db.CatalogDao
import com.epishie.spacial.model.db.CatalogData
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Flowable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class CatalogRepositoryTest {
    @Rule @JvmField
    val taskRule = InstantTaskExecutorRule()

    lateinit var catalogRepository: CatalogRepository
    @MockK
    lateinit var catalogDao: CatalogDao
    val testScheduler = TestScheduler()
    val testCatalogEntity1 = CatalogEntity("test catalog 1",
            "http://test1.catalog")
    val testCatalogEntity2 = CatalogEntity("test catalog 2",
            "http://test2.catalog")
    val testCatalogData1 = CatalogData(0,
            "test catalog 1",
            "http://test1.catalog")
    val testCatalogData2 = CatalogData(0,
            "test catalog 2",
            "http://test2.catalog")

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        catalogRepository = CatalogRepository(catalogDao, testScheduler)
    }

    @Test
    fun addCatalogSuccess() {
        every { catalogDao.insert(testCatalogData1) } returns Unit
        val observer = catalogRepository.addCatalog(testCatalogEntity1)
                .test()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assertThat(observer.completions())
                .isEqualTo(1)
        assertThat(observer.errors().toList())
                .isEmpty()
    }

    @Test
    fun addCatalogDatabaseError() {
        every { catalogDao.insert(testCatalogData1) } throws SQLiteException()
        val observer = catalogRepository.addCatalog(testCatalogEntity1)
                .test()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assertThat(observer.completions())
                .isEqualTo(0)
        assertThat(observer.errors().toList())
                .hasOnlyElementsOfType(DatabaseError::class.java)
    }

    @Test
    fun addCatalogError() {
        every { catalogDao.insert(testCatalogData1) } throws IllegalStateException()
        val observer = catalogRepository.addCatalog(testCatalogEntity1)
                .test()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assertThat(observer.completions())
                .isEqualTo(0)
        assertThat(observer.errors().toList())
                .hasOnlyElementsOfType(IllegalStateException::class.java)
    }

    @Test
    fun getCatalogSuccess() {
        every { catalogDao.selectCatalog("test catalog 1") } returns
                Flowable.just(listOf(testCatalogData1))
        val observer = catalogRepository.getCatalog("test catalog 1")
                .test()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assertThat(observer.values().toMutableList())
                .containsExactly(listOf(testCatalogEntity1))
        assertThat(observer.errors().toList())
                .isEmpty()
    }

    @Test
    fun getCatalogError() {
        every { catalogDao.selectCatalog("test catalog 1") } returns
                Flowable.error(SQLiteException())
        val observer = catalogRepository.getCatalog("test catalog 1")
                .test()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assertThat(observer.values().toMutableList())
                .isEmpty()
        assertThat(observer.errors().toList())
                .hasOnlyElementsOfType(DatabaseError::class.java)
    }

    @Test
    fun getCatalogsSuccess() {
        val daoFactory = object : DataSource.Factory<Int, CatalogData>() {
            override fun create(): DataSource<Int, CatalogData> {
                return MockDataSource(listOf(testCatalogData1, testCatalogData2))
            }
        }
        every { catalogDao.selectAll() } returns daoFactory
        val catalogs = LivePagedListBuilder(catalogRepository.getCatalogs(), 10)
                .build()
        catalogs.observeForever {  }

        assertThat(catalogs.value)
                .containsExactly(testCatalogEntity1, testCatalogEntity2)
    }

    @Test
    fun deleteCatalogSuccess() {
        every { catalogDao.deleteCatalog("sample") } returns Unit
        val observer = catalogRepository.deleteCatalog("sample")
                .test()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assertThat(observer.completions())
                .isEqualTo(1)
        assertThat(observer.errors().toList())
                .isEmpty()
    }

    @Test
    fun deleteCatalogDatabaseError() {
        every { catalogDao.deleteCatalog("sample") } throws SQLiteException()
        val observer = catalogRepository.deleteCatalog("sample")
                .test()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assertThat(observer.completions())
                .isEqualTo(0)
        assertThat(observer.errors().toList())
                .hasOnlyElementsOfType(DatabaseError::class.java)
    }

    @Test
    fun deleteCatalogError() {
        val error = IllegalStateException()
        every { catalogDao.deleteCatalog("sample") } throws error
        val observer = catalogRepository.deleteCatalog("sample")
                .test()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assertThat(observer.completions())
                .isEqualTo(0)
        assertThat(observer.errors().toList())
                .containsExactly(error)
    }

    private class MockDataSource(val value: List<CatalogData>) : ItemKeyedDataSource<Int, CatalogData>() {
        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<CatalogData>) {
            callback.onResult(value)
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<CatalogData>) { }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<CatalogData>) { }

        override fun getKey(item: CatalogData): Int {
            return value.indexOf(item)
        }
    }
}