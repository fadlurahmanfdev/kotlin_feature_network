package com.fadlurahmanfdev.example.data.dto.exception

data class FeatureException(
    val title: String,
    override val message: String?,
) : Throwable(message = message)
