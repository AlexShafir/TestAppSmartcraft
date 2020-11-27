package com.test.app.di

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {
    private val applicationInjector = DaggerAppComponent.builder().application(this).context(this).build()
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = applicationInjector
}