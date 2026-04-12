package com.fadlurahmanfdev.example.data.repository

import com.fadlurahmanfdev.example.data.dto.response.PostResponse
import io.reactivex.rxjava3.core.Observable

interface RepositoryDatasource {
    fun getPostByIdUsingCorrectPinningPublicKey(id:Int): Observable<PostResponse>
    fun getPostByIdUsingIncorrectPinningPublicKey(id:Int): Observable<PostResponse>
    fun getPostByIdUsingCorrectCertFromResource(id:Int): Observable<PostResponse>
    fun getPostByIdUsingIncorrectCertFromResource(id:Int): Observable<PostResponse>
    fun getPostByIdUsingCorrectFingerprint(id:Int): Observable<PostResponse>
    fun getPostByIdUsingIncorrectFingerprint(id:Int): Observable<PostResponse>
    fun getPostByIdWithRetrySSLMechanism(id:Int): Observable<PostResponse>
}