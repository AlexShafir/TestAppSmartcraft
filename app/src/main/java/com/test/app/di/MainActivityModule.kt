package com.test.app.di

import com.test.app.presentation.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(
        modules = [
            FragmentBuildersModule::class,
            LoginModule::class
        ]
    )
    abstract fun contributeMainActivity(): MainActivity

}