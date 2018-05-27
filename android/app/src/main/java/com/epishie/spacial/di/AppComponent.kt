package com.epishie.spacial.di

import com.epishie.spacial.App
import com.epishie.spacial.ui.features.discover.DiscoverFragment
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

    fun inject(fragment: DiscoverFragment)
}