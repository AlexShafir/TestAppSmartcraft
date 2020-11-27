package com.test.app.di

import com.test.app.presentation.contacts.ContactsFragment
import com.test.app.presentation.login.LoginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeContactsFragment(): ContactsFragment

}