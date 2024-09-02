package com.fadlurahmanfdev.example.data.api

import com.fadlurahmanfdev.example.data.dto.request.CreateGuestSessionRequest
import com.fadlurahmanfdev.example.data.dto.response.BaseIdentityBankMasResponse
import com.fadlurahmanfdev.example.data.dto.response.CreateGuestSessionResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface BankMasIdentityAPI {
    @POST("identity-service/guest/session/create")
    fun generateGuestSession(
        @Body request: CreateGuestSessionRequest
    ): Observable<BaseIdentityBankMasResponse<CreateGuestSessionResponse>>
}