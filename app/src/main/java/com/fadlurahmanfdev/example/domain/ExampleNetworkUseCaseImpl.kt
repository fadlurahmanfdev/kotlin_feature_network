package com.fadlurahmanfdev.example.domain

import com.fadlurahmanfdev.example.data.dto.request.CreateGuestSessionRequest
import com.fadlurahmanfdev.example.data.dto.response.CreateGuestSessionResponse
import com.fadlurahmanfdev.example.data.repository.RepositoryDatasourceImpl
import io.reactivex.rxjava3.core.Observable
import java.util.UUID

class ExampleNetworkUseCaseImpl(
    private val repositoryDatasourceImpl: RepositoryDatasourceImpl
) : ExampleNetworkUseCase {
    override fun generateGuestSession(): Observable<CreateGuestSessionResponse> {
        val request = CreateGuestSessionRequest(
            guestId = UUID.randomUUID().toString()
        )
        return repositoryDatasourceImpl.generateGuestSession(request).map { it ->
            it.data
        }
    }
}