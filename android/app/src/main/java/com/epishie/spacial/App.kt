package com.epishie.spacial

import android.app.Application
import android.content.Context
import com.epishie.spacial.di.AppComponent
import com.epishie.spacial.di.DaggerAppComponent

class App : Application() {
    lateinit var component: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder()
                .appModule(this)
                .build()
    }
}

val Context.component: AppComponent
    get() = (this.applicationContext as App).component