package co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.api

import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.request.CreateGuestSessionRequest
import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.response.BaseIdentityBankMasResponse
import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.response.CreateGuestSessionResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface BankMasIdentityAPI {
    @POST("identity-service/guest/session/create")
    fun generateGuestSession(
        @Body request: CreateGuestSessionRequest
    ): Observable<BaseIdentityBankMasResponse<CreateGuestSessionResponse>>
}