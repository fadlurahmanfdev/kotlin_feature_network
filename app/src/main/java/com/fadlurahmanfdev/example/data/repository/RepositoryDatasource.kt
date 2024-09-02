package com.fadlurahmanfdev.example.data.repository

import com.fadlurahmanfdev.example.data.dto.request.CreateGuestSessionRequest
import com.fadlurahmanfdev.example.data.dto.response.BaseIdentityBankMasResponse
import com.fadlurahmanfdev.example.data.dto.response.CreateGuestSessionResponse
import io.reactivex.rxjava3.core.Observable

interface RepositoryDatasource {
    fun generateGuestSession(request: CreateGuestSessionRequest): Observable<BaseIdentityBankMasResponse<CreateGuestSessionResponse>>
}