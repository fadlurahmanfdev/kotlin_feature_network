package com.fadlurahmanfdev.example.data.repository

import com.fadlurahmanfdev.example.data.api.JsonPlaceHolderAPI
import com.fadlurahmanfdev.example.data.dto.response.PostResponse
import io.reactivex.rxjava3.core.Observable
import okio.IOException

class RepositoryDatasourceImpl(
    private val jsonPlaceHolderAPIWithCorrectPinningPublicKey: JsonPlaceHolderAPI,
    private val jsonPlaceHolderAPIWithIncorrectPinningPublicKey: JsonPlaceHolderAPI,
    private val jsonPlaceHolderAPIWithCorrectCertFromResource: JsonPlaceHolderAPI,
    private val jsonPlaceHolderAPIWithIncorrectCertFromResource: JsonPlaceHolderAPI,
    private val jsonPlaceHolderAPIWithCorrectFingerprint: JsonPlaceHolderAPI,
    private val jsonPlaceHolderAPIWithIncorrectFingerprint: JsonPlaceHolderAPI,
    private val jsonPlaceHolderAPIWithRetryMechanism: JsonPlaceHolderAPI,
) : RepositoryDatasource {
    override fun getPostByIdUsingCorrectPinningPublicKey(id: Int): Observable<PostResponse> {
        return jsonPlaceHolderAPIWithCorrectPinningPublicKey.getPostById(id).map { response ->
            if (!response.isSuccessful) {
                throw IOException("")
            }

            if (response.body() == null) {
                throw IOException()
            }

            response.body()!!
        }
    }

    override fun getPostByIdUsingIncorrectPinningPublicKey(id: Int): Observable<PostResponse> {
        return jsonPlaceHolderAPIWithIncorrectPinningPublicKey.getPostById(id).map { response ->
            if (!response.isSuccessful) {
                throw IOException("")
            }

            if (response.body() == null) {
                throw IOException()
            }

            response.body()!!
        }
    }

    override fun getPostByIdUsingCorrectCertFromResource(id: Int): Observable<PostResponse> {
        return jsonPlaceHolderAPIWithCorrectCertFromResource.getPostById(id).map { response ->
            if (!response.isSuccessful) {
                throw IOException("")
            }

            if (response.body() == null) {
                throw IOException()
            }

            response.body()!!
        }
    }

    override fun getPostByIdUsingIncorrectCertFromResource(id: Int): Observable<PostResponse> {
        return jsonPlaceHolderAPIWithIncorrectCertFromResource.getPostById(id).map { response ->
            if (!response.isSuccessful) {
                throw IOException("")
            }

            if (response.body() == null) {
                throw IOException()
            }

            response.body()!!
        }
    }

    override fun getPostByIdUsingCorrectFingerprint(id: Int): Observable<PostResponse> {
        return jsonPlaceHolderAPIWithCorrectFingerprint.getPostById(id).map { response ->
            if (!response.isSuccessful) {
                throw IOException("")
            }

            if (response.body() == null) {
                throw IOException()
            }

            response.body()!!
        }
    }

    override fun getPostByIdUsingIncorrectFingerprint(id: Int): Observable<PostResponse> {
        return jsonPlaceHolderAPIWithIncorrectFingerprint.getPostById(id).map { response ->
            if (!response.isSuccessful) {
                throw IOException("")
            }

            if (response.body() == null) {
                throw IOException()
            }

            response.body()!!
        }
    }

    override fun getPostByIdWithRetrySSLMechanism(id: Int): Observable<PostResponse> {
        return jsonPlaceHolderAPIWithRetryMechanism.getPostById(id).map { response ->
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