package com.fadlurahmanfdev.networx.exception

data class NetworxException(
    val code: String,
    override val message: String?,
) : Throwable(message = message)
