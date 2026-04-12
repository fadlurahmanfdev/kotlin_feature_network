package com.fadlurahmanfdev.example.domain.usecase

import com.fadlurahmanfdev.example.data.dto.response.PostResponse
import com.fadlurahmanfdev.example.data.repository.RepositoryDatasource
import io.reactivex.rxjava3.core.Observable

class ExampleNetworkUseCaseImpl(
    private val repositoryDatasource: RepositoryDatasource
) : ExampleNetworkUseCase {
    override fun getPostByIdWithCorrectPinningPublicKey(id: Int): Observable<PostResponse> {
        return repositoryDatasource.getPostByIdUsingCorrectPinningPublicKey(id)
    }

    override fun getPostByIdWithIncorrectPinningPublicKey(id: Int): Observable<PostResponse> {
        return repositoryDatasource.getPostByIdUsingIncorrectPinningPublicKey(id)
    }

    override fun getPostByIdWithCorrectCertFromResource(id: Int): Observable<PostResponse> {
        return repositoryDatasource.getPostByIdUsingCorrectCertFromResource(id)
    }

    override fun getPostByIdWithIncorrectCertFromResource(id: Int): Observable<PostResponse> {
        return repositoryDatasource.getPostByIdUsingIncorrectCertFromResource(id)
    }

    override fun getPostByIdWithCorrectFingerprint(id: Int): Observable<PostResponse> {
        return repositoryDatasource.getPostByIdUsingCorrectFingerprint(id)
    }

    override fun getPostByIdWithIncorrectFingerprint(id: Int): Observable<PostResponse> {
        return repositoryDatasource.getPostByIdUsingIncorrectFingerprint(id)
    }

    override fun getPostByIdWithRetrySSLMechanism(id: Int): Observable<PostResponse> {
        return repositoryDatasource.getPostByIdWithRetrySSLMechanism(id)
    }
}