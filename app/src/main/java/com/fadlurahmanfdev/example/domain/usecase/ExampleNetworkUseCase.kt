package com.fadlurahmanfdev.example.domain.usecase

import com.fadlurahmanfdev.example.data.dto.response.PostResponse
import io.reactivex.rxjava3.core.Observable

interface ExampleNetworkUseCase {
    fun getPostByIdWithCorrectPinningPublicKey(id:Int): Observable<PostResponse>
    fun getPostByIdWithIncorrectPinningPublicKey(id:Int): Observable<PostResponse>
    fun getPostByIdWithCorrectCertFromResource(id:Int): Observable<PostResponse>
    fun getPostByIdWithIncorrectCertFromResource(id:Int): Observable<PostResponse>
    fun getPostByIdWithCorrectFingerprint(id:Int): Observable<PostResponse>
    fun getPostByIdWithIncorrectFingerprint(id:Int): Observable<PostResponse>
    fun getPostByIdWithRetrySSLMechanism(id:Int): Observable<PostResponse>
}