package com.fadlurahmanfdev.example.data.dto.response

data class BaseIdentityBankMasResponse<T>(
    val message: String,
    val data: T,
)
