package com.test.app.di

import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.test.app.R
import com.test.app.core.data.*
import com.test.app.core.domain.interactors.*
import com.test.app.core.domain.usecases.*
import com.test.app.framework.*
import com.test.app.framework.db.AppDatabase
import com.test.app.framework.db.ContactsDao
import com.test.app.framework.network.AuthorizationInterceptor
import com.test.app.framework.network.IRefreshTokenService
import com.test.app.framework.network.OAuth2Authenticator
import com.test.app.framework.network.RefreshTokenService
import com.test.app.presentation.IScreenNavigator
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class AppModule {

    // Named
    companion object {
        const val CLIENT_ID = "ClientId"
        const val CLIENT_SECRET = "ClientSecret"
    }

    @Named(CLIENT_ID)
    @Provides
    @Singleton
    fun provideClientId(context: Context): String {
        return context.getString(R.string.server_client_id)
    }

    @Named(CLIENT_SECRET)
    @Provides
    @Singleton
    fun provideClientSecret(context: Context): String {
        return context.getString(R.string.server_client_secret)
    }

    // Use cases

    @Singleton
    @Provides
    fun provideIContactsUC(repo: IContacts): IContactsUC {
        return ContactsUC(repo)
    }

    @Singleton
    @Provides
    fun provideIScreenSelectorUC(tokenStorage: ITokenStorage): IScreenSelectorUC {
        return ScreenSelectorUC(tokenStorage)
    }

    // Models

    @Singleton
    @Provides
    fun provideContactsDao(db: AppDatabase): ContactsDao {
        return db.contactsDao()
    }

    @Singleton
    @Provides
    fun provideIContacts(peopleApi: GooglePeopleApi, contactsDao: ContactsDao): IContacts {
        return Contacts(peopleApi, contactsDao)
    }

    @Singleton
    @Provides
    fun provideIScreenNavigator(): IScreenNavigator {
        return ScreenNavigator()
    }

    @Singleton
    @Provides
    fun provideIConnection(context: Context): IConnection {
        return Connection(context)
    }

    @Singleton
    @Provides
    fun provideITokenStorage(context: Context): ITokenStorage {
        return TokenStorage(context)
    }

    @Singleton
    @Provides
    fun provideIRefreshTokenService(
        oauth: OAuth2Api,
        @Named(CLIENT_ID) clientId: String,
        @Named(CLIENT_SECRET) clientSecret: String
    ): IRefreshTokenService {
        return RefreshTokenService(oauth, clientId, clientSecret)
    }

    @Singleton
    @Provides
    fun provideGoogleLoginModel(
        server: OAuth2Api,
        @Named(CLIENT_ID) clientId: String,
        @Named(CLIENT_SECRET) clientSecret: String,
    ): GoogleLoginModel {
        return GoogleLoginModel(server, clientId, clientSecret)
    }

    @Singleton
    @Provides
    fun provideActivityResultClient(glm: GoogleLoginModel): ActivityResultClient {
        return glm
    }

    @Singleton
    @Provides
    fun provideIntentLoginModel(glm: GoogleLoginModel): IntentLoginModel {
        return glm
    }

    @Singleton
    @Provides
    fun provideILoginUC(loginModel: IntentLoginModel, tokenStorage: ITokenStorage, contacts:IContacts): ILoginUC {
        return LoginUC(loginModel, tokenStorage, contacts)
    }

    @Singleton
    @Provides
    fun provideIOnTokenRevokedUC(tokenStorage: ITokenStorage):IOnTokenRevokedUC {
        return OnTokenRevokedUC(tokenStorage)
    }

    // Expensive Models

    @Singleton
    @Provides
    fun provideDb(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "TestAppDb"
        ).build()
    }

    @Singleton
    @Provides
    fun provideOAuth2Api(): OAuth2Api {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/oauth2/v4/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(OAuth2Api::class.java)
    }

    @Singleton
    @Provides
    fun provideGooglePeopleApi(
        authorizationInterceptor: AuthorizationInterceptor,
        oauthAuthenticator: OAuth2Authenticator
    ): GooglePeopleApi {

        val authOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(authorizationInterceptor)
            .authenticator(oauthAuthenticator)
            .build()

        val r = Retrofit.Builder()
            .client(authOkHttpClient)
            .baseUrl("https://people.googleapis.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(GooglePeopleApi::class.java)


        return r
    }

}
