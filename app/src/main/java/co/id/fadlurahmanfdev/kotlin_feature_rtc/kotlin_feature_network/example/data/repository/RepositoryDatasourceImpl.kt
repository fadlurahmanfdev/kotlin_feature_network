package co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.repository

import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.api.BankMasIdentityAPI
import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.request.CreateGuestSessionRequest
import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.response.BaseIdentityBankMasResponse
import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.response.CreateGuestSessionResponse
import io.reactivex.rxjava3.core.Observable

class RepositoryDatasourceImpl(
    private val identityBankMasApi: BankMasIdentityAPI
) : RepositoryDatasource {
    override fun generateGuestSession(request: CreateGuestSessionRequest): Observable<BaseIdentityBankMasResponse<CreateGuestSessionResponse>> {
        return identityBankMasApi.generateGuestSession(request)
    }
}