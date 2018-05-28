package com.epishie.spacial.di

import com.epishie.spacial.App
import com.epishie.spacial.ui.features.catalog.CatalogFragment
import com.epishie.spacial.ui.features.catalog.CatalogListFragment
import com.epishie.spacial.ui.features.discover.DiscoverFragment
import com.epishie.spacial.ui.features.image.ImageFragment
import com.epishie.spacial.ui.features.main.MainFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        @BindsInstance
        fun appModule(app: App): Builder
    }

    fun inject(fragment: MainFragment)
    fun inject(fragment: DiscoverFragment)
    fun inject(fragment: ImageFragment)
    fun inject(fragment: CatalogFragment)
    fun inject(fragment: CatalogListFragment)
}