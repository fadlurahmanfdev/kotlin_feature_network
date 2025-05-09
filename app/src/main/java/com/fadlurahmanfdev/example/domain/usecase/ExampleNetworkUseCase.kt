package com.fadlurahmanfdev.example.domain.usecase

import com.fadlurahmanfdev.example.data.dto.response.PostResponse
import io.reactivex.rxjava3.core.Observable

interface ExampleNetworkUseCase {
    fun getPostById(id:Int): Observable<PostResponse>
    fun getPostByIdIncorrectSSL(id:Int): Observable<PostResponse>
    fun getPostByIdRetryIncorrectSSL(id:Int): Observable<PostResponse>
}