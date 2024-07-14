package co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.repository

import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.request.CreateGuestSessionRequest
import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.response.BaseIdentityBankMasResponse
import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.response.CreateGuestSessionResponse
import io.reactivex.rxjava3.core.Observable

interface RepositoryDatasource {
    fun generateGuestSession(request: CreateGuestSessionRequest): Observable<BaseIdentityBankMasResponse<CreateGuestSessionResponse>>
}