package com.example.toDoMariem.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface UserWebService {
    @Multipart
    @POST("sync/v9/update_avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): Response<User>
    @GET("/sync/v9/user/")
    suspend fun fetchUser(): Response<User>
    @PATCH("sync/v9/sync")
    suspend fun update(@Body userUpdate: UserUpdate): Response<Unit>
}
@Serializable
data class UserUpdate(
    @SerialName("full_name") val name: String)
