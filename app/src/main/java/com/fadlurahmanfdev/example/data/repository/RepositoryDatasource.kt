package com.fadlurahmanfdev.example.data.repository

import com.fadlurahmanfdev.example.data.dto.response.PostResponse
import io.reactivex.rxjava3.core.Observable

interface RepositoryDatasource {
    fun getPostById(id:Int): Observable<PostResponse>
}