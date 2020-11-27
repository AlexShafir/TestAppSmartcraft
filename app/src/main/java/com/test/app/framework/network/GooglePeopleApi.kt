package com.test.app.core.data

import androidx.annotation.Nullable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

data class Photo(
    val url:String,
    val default:Boolean
)

data class Organization(
    val name:String
)

data class Name(
    val displayName:String
)

data class Person(
    val resourceName:String,
    val names:Array<Name>,
    val photos:Array<Photo>,
    val organizations:Array<Organization>?
)

data class PeopleApiResponse(
    val connections:Array<Person>,
    val totalItems:Int
)

/**
 * See: https://developers.google.com/people/v1/contacts#list-the-users-contacts
 */
interface GooglePeopleApi {

    @GET("people/me/connections")
    @Headers("Host: people.googleapis.com")
    suspend fun listAllContacts(
        @Query("personFields", encoded = true) personFields:String,
    ):PeopleApiResponse

}