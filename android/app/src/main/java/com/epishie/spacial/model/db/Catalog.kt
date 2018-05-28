package com.epishie.spacial.model.db

import android.arch.paging.DataSource
import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface CatalogDao {
    @Insert
    fun insert(catalog: CatalogData)
    @Query("SELECT * FROM catalogs")
    fun selectAll(): DataSource.Factory<Int, CatalogData>
    @Query("SELECT * FROM catalogs WHERE name = :name")
    fun selectCatalog(name: String): Flowable<List<CatalogData>>
    @Query("DELETE FROM catalogs WHERE name = :name")
    fun deleteCatalog(name: String)
}

@Entity(tableName = "catalogs", indices = [Index("name", unique = true)])
data class CatalogData(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String,
        @ColumnInfo()
        val coverUrl: String)