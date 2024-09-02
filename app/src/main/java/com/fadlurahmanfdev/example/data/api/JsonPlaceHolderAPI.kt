package com.fadlurahmanfdev.example.data.api

import com.fadlurahmanfdev.example.data.dto.response.PostResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JsonPlaceHolderAPI {
    @GET("posts/{id}")
    fun getPostById(
        @Path("id") id:Int
    ): Observable<Response<PostResponse>>
}