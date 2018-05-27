package com.epishie.spacial.di

import android.content.Context
import com.epishie.spacial.App
import com.epishie.spacial.model.ImageRepository
import com.epishie.spacial.model.api.NasaApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideContext(app: App): Context {
        return app
    }

    @Singleton
    @Provides
    fun provideImagesApi(): ImageRepository.ImagesApi {
        return NasaApi()
    }
}