package co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.domain

import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.response.CreateGuestSessionResponse
import io.reactivex.rxjava3.core.Observable

interface ExampleNetworkUseCase {
    fun generateGuestSession(): Observable<CreateGuestSessionResponse>
}