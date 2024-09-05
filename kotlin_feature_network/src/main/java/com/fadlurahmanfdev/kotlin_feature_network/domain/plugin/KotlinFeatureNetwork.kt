package com.fadlurahmanfdev.kotlin_feature_network.domain.plugin

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class KotlinFeatureNetwork {
    companion object {
        fun getTrustManagerFromResource(
            context: Context,
            @RawRes certificateResource: Int,
            alias: String,
        ): Array<TrustManager> {
            // Load CAs from an InputStream
            val certificateFactory = CertificateFactory.getInstance("X.509")

            // Load the example.com certificate from the resources
            val certificateInputStream: InputStream =
                context.resources.openRawResource(certificateResource)
            val certificate: X509Certificate =
                certificateFactory.generateCertificate(certificateInputStream) as X509Certificate
            certificateInputStream.close()

            // Create a KeyStore containing our trusted CAs
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null) // Initialize the KeyStore with no password
                setCertificateEntry(alias, certificate)
            }

            // Create a TrustManager that trusts the CAs in our KeyStore
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                    init(keyStore)
                }

            return trustManagerFactory.trustManagers
        }

        @SuppressLint("CustomX509TrustManager")
        fun getTrustManager(x509TrustManager: X509TrustManager? = null): Array<TrustManager> {
            return arrayOf<TrustManager>(x509TrustManager ?: object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                    Log.d(
                        KotlinFeatureNetwork::class.java.simpleName,
                        "checkClientTrusted - auth type: $authType"
                    )
                    Log.d(
                        KotlinFeatureNetwork::class.java.simpleName,
                        "checkClientTrusted - chain size: ${chain?.size ?: 0}"
                    )
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                    Log.d(
                        KotlinFeatureNetwork::class.java.simpleName,
                        "checkServerTrusted - auth type: $authType"
                    )
                    Log.d(
                        KotlinFeatureNetwork::class.java.simpleName,
                        "checkServerTrusted- chain size: ${chain?.size ?: 0}"
                    )
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

            })
        }

        fun getSslSocketFactory(trustManagers: Array<TrustManager>): SSLSocketFactory {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustManagers, SecureRandom())
            return sslContext.socketFactory
        }

        fun getOkHttpClientBuilder(
            connectTimeout: Long?,
            readTimeout: Long?,
            writeTimeout: Long?,
            useLoggingInterceptor: Boolean,
            certificatePinner: CertificatePinner?,
            sslSocketFactory: SSLSocketFactory?,
            x509TrustManager: X509TrustManager?,
            hostnameVerifier: HostnameVerifier?,
        ): OkHttpClient.Builder {
            return OkHttpClient.Builder().apply {
                if (connectTimeout != null) {
                    this.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                }

                if (readTimeout != null) {
                    this.readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                }

                if (writeTimeout != null) {
                    this.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                }

                if (sslSocketFactory != null || x509TrustManager != null || hostnameVerifier != null) {
                    assert(sslSocketFactory != null)
                    assert(x509TrustManager != null)
                    assert(hostnameVerifier != null)
                }

                if (sslSocketFactory != null && x509TrustManager != null && hostnameVerifier != null) {
                    this.sslSocketFactory(sslSocketFactory, x509TrustManager)
                    this.hostnameVerifier(hostnameVerifier)
                }

                if (certificatePinner != null) {
                    this.certificatePinner(certificatePinner)
                }

                if (useLoggingInterceptor) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        },
                    )
                }
            }
        }
    }
}