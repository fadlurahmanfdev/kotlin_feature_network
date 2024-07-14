package co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.domain

import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.request.CreateGuestSessionRequest
import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.response.CreateGuestSessionResponse
import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.repository.RepositoryDatasourceImpl
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