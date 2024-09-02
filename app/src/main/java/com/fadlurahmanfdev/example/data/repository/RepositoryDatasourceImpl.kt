package com.fadlurahmanfdev.example.data.repository

import com.fadlurahmanfdev.example.data.api.BankMasIdentityAPI
import com.fadlurahmanfdev.example.data.dto.request.CreateGuestSessionRequest
import com.fadlurahmanfdev.example.data.dto.response.BaseIdentityBankMasResponse
import com.fadlurahmanfdev.example.data.dto.response.CreateGuestSessionResponse
import io.reactivex.rxjava3.core.Observable

class RepositoryDatasourceImpl(
    private val identityBankMasApi: BankMasIdentityAPI
) : RepositoryDatasource {
    override fun generateGuestSession(request: CreateGuestSessionRequest): Observable<BaseIdentityBankMasResponse<CreateGuestSessionResponse>> {
        return identityBankMasApi.generateGuestSession(request)
    }
}