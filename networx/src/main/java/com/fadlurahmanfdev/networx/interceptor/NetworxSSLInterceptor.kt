package com.fadlurahmanfdev.networx.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

abstract class NetworxSSLInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        try {
            return chain.proceed(request)
        } catch (e: SSLHandshakeException) {
            Log.e(this::class.java.simpleName, "Networx-LOG %%% failed to handshake connection")
            return onSSLHandshakeException(chain, e)
        } catch (e: SSLPeerUnverifiedException) {
            Log.e(this::class.java.simpleName, "Networx-LOG %%% failed to verify ssl")
            return onSSLPeerUnverifiedException(chain, e)
        } catch (e: Exception) {
            throw e
        }
    }

    abstract fun onSSLPeerUnverifiedException(
        chain: Interceptor.Chain,
        e: SSLPeerUnverifiedException,
    ): Response

    abstract fun onSSLHandshakeException(
        chain: Interceptor.Chain,
        e: SSLHandshakeException,
    ): Response
}