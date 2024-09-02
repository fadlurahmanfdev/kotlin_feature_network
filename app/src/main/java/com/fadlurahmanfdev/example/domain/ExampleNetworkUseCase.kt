package com.fadlurahmanfdev.example.domain

import com.fadlurahmanfdev.example.data.dto.response.CreateGuestSessionResponse
import io.reactivex.rxjava3.core.Observable

interface ExampleNetworkUseCase {
    fun generateGuestSession(): Observable<CreateGuestSessionResponse>
}