package com.fadlurahmanfdev.example.domain.interceptor

import android.content.Context
import com.fadlurahmanfdev.example.data.dto.exception.FeatureException
import com.fadlurahmanfdev.kotlin_feature_network.data.repository.FeatureNetworkRepository
import com.fadlurahmanfdev.kotlin_feature_network.domain.interceptor.SSLInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

class ExampleSSLInterceptor(
    private val context: Context,
    private val client: OkHttpClient,
    private val networkRepository: FeatureNetworkRepository
) : SSLInterceptor() {
    override fun onSSLPeerUnverifiedException(
        chain: Interceptor.Chain,
        e: SSLPeerUnverifiedException
    ): Response {
        val request = chain.request()
        val certificatePinner = networkRepository.getCertificatePinnerBuilder()
            .add(
                "jsonplaceholder.typicode.com",
                "sha256/IcwtGuxd2fA2t1B0ylJrjvtQm4g4vz5aVshokMHp2Qc=",
                "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=",
                "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c="
            )
            .build()
        return client.newBuilder().certificatePinner(certificatePinner)
            .addInterceptor(networkRepository.getChuckerInterceptorBuilder(context).build())
            .build().newCall(request.newBuilder().addHeader("X-Retry", "true").build()).execute()
    }

    override fun onSSLHandshakeException(
        chain: Interceptor.Chain,
        e: SSLHandshakeException
    ): Response {
        throw FeatureException("TES TITLE SSL", "TES MESSAGE SSL")
    }


}