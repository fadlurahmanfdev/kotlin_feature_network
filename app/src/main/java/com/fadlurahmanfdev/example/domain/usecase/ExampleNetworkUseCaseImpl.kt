package com.fadlurahmanfdev.example.domain.usecase

import com.fadlurahmanfdev.example.data.dto.response.PostResponse
import com.fadlurahmanfdev.example.data.repository.RepositoryDatasource
import io.reactivex.rxjava3.core.Observable

class ExampleNetworkUseCaseImpl(
    private val repositoryDatasource: RepositoryDatasource
) : ExampleNetworkUseCase {
    override fun getPostById(id: Int): Observable<PostResponse> {
        return repositoryDatasource.getPostById(id)
    }

    override fun getPostByIdIncorrectSSL(id: Int): Observable<PostResponse> {
        return repositoryDatasource.getPostByIdIncorrectSSL(id)
    }

    override fun getPostByIdRetryIncorrectSSL(id: Int): Observable<PostResponse> {
        return repositoryDatasource.getPostByIdRetryIncorrectSSL(id)
    }
}