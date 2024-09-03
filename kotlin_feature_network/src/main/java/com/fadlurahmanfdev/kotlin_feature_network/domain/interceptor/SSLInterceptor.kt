package com.fadlurahmanfdev.kotlin_feature_network.domain.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

abstract class SSLInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        try {
            return chain.proceed(request)
        } catch (e: SSLHandshakeException) {
            return onSSLHandshakeException(chain, e)
        } catch (e: SSLPeerUnverifiedException){
            return  onSSLPeerUnverifiedException(chain, e)
        } catch (e: Exception) {
            throw e;
        }
    }

    abstract fun onSSLPeerUnverifiedException(
        chain: Interceptor.Chain,
        e: SSLPeerUnverifiedException
    ): Response

    abstract fun onSSLHandshakeException(chain: Interceptor.Chain, e: SSLHandshakeException): Response
}