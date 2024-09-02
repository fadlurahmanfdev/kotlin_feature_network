package com.fadlurahmanfdev.example.domain

import com.fadlurahmanfdev.example.data.dto.response.PostResponse
import io.reactivex.rxjava3.core.Observable

interface ExampleNetworkUseCase {
    fun getPostById(id:Int): Observable<PostResponse>
}