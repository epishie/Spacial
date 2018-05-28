package com.epishie.spacial.model.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.epishie.spacial.component
import java.util.concurrent.Executors

@Database(entities = [CatalogData::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "db.db")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            initializeDb(context.component.getAppDatabase())
                        }
                    })
                    .build()
        }

        // Initial data
        private fun initializeDb(appDatabase: AppDatabase) {
            val catalogDao = appDatabase.catalogDao()
            Executors.newSingleThreadExecutor().execute {
                catalogDao.insert(CatalogData(0, "Mars",
                        "https://images-assets.nasa.gov/image/PIA07081/PIA07081~thumb.jpg"))
                catalogDao.insert(CatalogData(0, "Solar Flares",
                        "https://images-assets.nasa.gov/image/PIA21958/PIA21958~thumb.jpg"))
                catalogDao.insert(CatalogData(0, "Voyager",
                        "https://images-assets.nasa.gov/image/PIA14111/PIA14111~thumb.jpg"))
            }
        }
    }

    abstract fun catalogDao(): CatalogDao
}