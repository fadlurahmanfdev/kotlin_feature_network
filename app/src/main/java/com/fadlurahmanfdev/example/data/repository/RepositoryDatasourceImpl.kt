package com.fadlurahmanfdev.example.data.repository

import com.fadlurahmanfdev.example.data.api.JsonPlaceHolderAPI
import com.fadlurahmanfdev.example.data.dto.response.PostResponse
import io.reactivex.rxjava3.core.Observable
import okio.IOException

class RepositoryDatasourceImpl(
    private val jsonPlaceHolderAPI: JsonPlaceHolderAPI,
    private val jsonPlaceHolderIncorrectSslAPI: JsonPlaceHolderAPI,
) : RepositoryDatasource {
    override fun getPostById(id: Int): Observable<PostResponse> {
        return jsonPlaceHolderAPI.getPostById(id).map { response ->
            if (!response.isSuccessful) {
                throw IOException("")
            }

            if (response.body() == null) {
                throw IOException()
            }

            response.body()!!
        }
    }

    override fun getPostByIdIncorrectSSL(id: Int): Observable<PostResponse> {
        return jsonPlaceHolderIncorrectSslAPI.getPostById(id).map { response ->
            if (!response.isSuccessful) {
                throw IOException("")
            }

            if (response.body() == null) {
                throw IOException()
            }

            response.body()!!
        }
    }
}