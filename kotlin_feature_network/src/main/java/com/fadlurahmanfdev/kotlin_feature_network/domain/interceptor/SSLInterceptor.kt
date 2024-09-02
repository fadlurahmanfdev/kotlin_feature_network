package com.fadlurahmanfdev.kotlin_feature_network.domain.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.net.ssl.SSLHandshakeException

abstract class SSLInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        try {
            return chain.proceed(request)
        } catch (e: SSLHandshakeException) {
            return onSSLException(chain, e)
        } catch (e: Exception) {
            throw e;
        }
    }

    abstract fun onSSLException(chain: Interceptor.Chain, e: SSLHandshakeException): Response
}