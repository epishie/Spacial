package com.epishie.spacial.model

import android.arch.paging.DataSource
import android.database.sqlite.SQLiteException
import com.epishie.spacial.model.db.CatalogDao
import com.epishie.spacial.model.db.CatalogData
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository(private val catalogDao: CatalogDao,
                        private val scheduler: Scheduler) {
    @Inject constructor(catalogDao: CatalogDao) : this(catalogDao, Schedulers.io())

    fun addCatalog(catalog: CatalogEntity): Completable {
        return Completable.fromAction {
            catalogDao.insert(mapCatalog(catalog))
        }.onErrorResumeNext {
            Completable.error(mapError(it))
        }.subscribeOn(scheduler)
    }

    fun getCatalog(name: String): Flowable<List<CatalogEntity>> {
        return catalogDao.selectCatalog(name)
                .map { it.map(this::mapCatalog) }
                .onErrorResumeNext { throwable: Throwable ->
                    Flowable.error(mapError(throwable))
                }
                .subscribeOn(scheduler)
    }

    fun getCatalogs(): DataSource.Factory<Int, CatalogEntity> {
        return catalogDao.selectAll().map(this::mapCatalog)
    }

    fun deleteCatalog(name: String): Completable {
        return Completable.fromAction {
            catalogDao.deleteCatalog(name)
        }.onErrorResumeNext {
            Completable.error(mapError(it))
        }.subscribeOn(scheduler)
    }

    private fun mapCatalog(catalogEntity: CatalogEntity): CatalogData {
        return catalogEntity.run {
            CatalogData(0, name, coverUrl)
        }
    }

    private fun mapCatalog(catalogData: CatalogData): CatalogEntity {
        return catalogData.run {
            CatalogEntity(name, coverUrl)
        }
    }

    private fun mapError(throwable: Throwable): Throwable {
        return when (throwable) {
            is SQLiteException -> DatabaseError()
            else -> throwable
        }
    }
}