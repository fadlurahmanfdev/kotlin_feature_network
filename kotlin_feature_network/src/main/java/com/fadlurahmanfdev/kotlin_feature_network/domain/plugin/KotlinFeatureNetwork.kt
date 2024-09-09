package com.fadlurahmanfdev.kotlin_feature_network.domain.plugin

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        /**
         * Retrieves an array of TrustManagers that trusts the certificate provided in the resources.
         *
         * This function loads an X.509 certificate from the application's raw resources, creates a
         * KeyStore containing the certificate, and initializes a TrustManagerFactory using the
         * certificate in the KeyStore. The resulting TrustManagers can be used to establish a secure
         * connection that trusts the loaded certificate.
         *
         * @param context The application context used to access the raw resources.
         * @param certificateResource The resource ID of the certificate (in .crt or .pem format)
         *                            stored in the 'raw' folder.
         *                            Must be annotated with @RawRes to ensure it refers to a raw resource.
         * @param alias A unique alias used to identify the certificate in the KeyStore.
         *              This alias should be meaningful, as it will be used to retrieve or manage
         *              the certificate in the future.
         * @return An array of TrustManagers initialized with the provided certificate.
         *
         * Usage:
         * val trustManagers = getTrustManagerFromResource(context, R.raw.my_certificate, "myAlias")
         */
        fun getTrustManagerFromResource(
            context: Context,
            @RawRes certificateResource: Int,
            alias: String,
        ): Array<TrustManager> {
            // Load CAs from an InputStream
            val certificateFactory = CertificateFactory.getInstance("X.509")

            // Load the certificate from the resources
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

        /**
         * Retrieves an array of TrustManagers, optionally using the provided X509TrustManager.
         * If no X509TrustManager is provided, a default implementation is used which logs details
         * about the certificate chain and authentication type.
         *
         * This function is useful when you need a TrustManager to manage certificate trust.
         * If no TrustManager is supplied, a fallback TrustManager is created that logs basic
         * information about the client and server certificate chains, but does not enforce any trust checks.
         *
         * @param x509TrustManager An optional custom X509TrustManager that can be passed to manage certificate
         *                         trust. If null, a default implementation is provided which only logs
         *                         certificate details.
         * @return An array of TrustManagers, containing either the provided X509TrustManager or the
         *         default implementation.
         *
         * Usage:
         * val trustManagers = getTrustManager() // Uses default TrustManager.
         * val trustManagers = getTrustManager(customTrustManager) // Uses a custom TrustManager.
         */
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

        /**
         * Creates an SSLSocketFactory using the provided TrustManagers.
         *
         * This function generates an SSLSocketFactory based on the array of TrustManagers passed as a parameter.
         * It is designed to work in conjunction with the `getTrustManagerFromResource` and `getTrustManager`
         * functions to enable SSL/TLS connections that can validate certificates, either from a resource or
         * from a default/custom TrustManager.
         *
         * @param trustManagers The array of TrustManagers used to validate SSL certificates. This array can
         *                      be obtained from the `getTrustManagerFromResource` or `getTrustManager`
         *                      functions.
         * @return An SSLSocketFactory initialized with the provided TrustManagers, which can be used
         *         to establish secure SSL/TLS connections.
         *
         * Usage:
         * val trustManagers = getTrustManagerFromResource(context, R.raw.my_certificate, "myAlias")
         * val sslSocketFactory = getSslSocketFactory(trustManagers)
         *
         * @see KotlinFeatureNetwork.getTrustManagerFromResource Retrieves TrustManagers from a certificate resource.
         * @see KotlinFeatureNetwork.getTrustManager Retrieves TrustManagers from a custom or default X509TrustManager.
         */
        fun getSslSocketFactory(trustManagers: Array<TrustManager>): SSLSocketFactory {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustManagers, SecureRandom())
            return sslContext.socketFactory
        }

        /**
         * Configures an OkHttpClient builder with options for logging, timeouts, SSL socket factory, certificate pinning, and hostname verification.
         * This function allows customization of the OkHttpClient’s behavior to suit specific networking and security requirements.
         *
         * @param connectTimeout Sets the maximum time allowed for establishing a connection, in milliseconds.
         *                       If null, the default timeout is used.
         * @param readTimeout Specifies the maximum time allowed for reading data from the server, in milliseconds.
         *                    If null, the default timeout is applied.
         * @param writeTimeout Determines the maximum time allowed for writing data to the server, in milliseconds.
         *                     If null, the default timeout is used.
         * @param useLoggingInterceptor Indicates whether to include a logging interceptor in the OkHttpClient.
         *                              When true, network requests and responses will be logged (useful for debugging).
         * @param certificatePinner Adds an SSL certificate pinner for enhanced security by pinning specific certificates.
         *                          If null, no certificate pinning is applied.
         * @param sslSocketFactory An optional SSLSocketFactory used to configure SSL/TLS connections. Typically created
         *                         using {@see KotlinFeatureNetwork.getSslSocketFactory}.
         * @param x509TrustManager An optional X509TrustManager used for SSL certificate trust. Can be retrieved using
         *                         {@see KotlinFeatureNetwork.getTrustManagerFromResource} or
         *                         {@see KotlinFeatureNetwork.getTrustManager}.
         * @param hostnameVerifier Optional hostname verifier for validating the hostname of the server.
         * @return OkHttpClient.Builder An OkHttpClient builder configured with the provided settings.
         *
         * Usage:
         * val clientBuilder = getOkHttpClientBuilder(
         *     connectTimeout = 10000L,
         *     readTimeout = 10000L,
         *     writeTimeout = 10000L,
         *     useLoggingInterceptor = true,
         *     certificatePinner = myCertificatePinner,
         *     sslSocketFactory = mySslSocketFactory,
         *     x509TrustManager = myTrustManager,
         *     hostnameVerifier = myHostnameVerifier
         * )
         *
         * @see KotlinFeatureNetwork.getTrustManagerFromResource Retrieves TrustManagers from a certificate resource.
         * @see KotlinFeatureNetwork.getTrustManager Retrieves TrustManagers from a custom or default X509TrustManager.
         * @see KotlinFeatureNetwork.getSslSocketFactory Creates an SSLSocketFactory using TrustManagers.
         */
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

        /**
         * Creates and configures a Retrofit API service for handling network requests.
         *
         * This function sets up the API by providing the base URL, network connection settings,
         * response format, and an interface that defines the API endpoints.
         *
         * @param baseUrl Specifies the main URL for the API. All requests will be directed to this address.
         *                For example, setting it to "https://api.example.com/" means that all requests will
         *                begin with this base URL.
         * @param okHttpClient The OkHttpClient instance that manages network connections, timeouts, logging,
         *                     and other customization options. You can create this client using
         *                     {@see KotlinFeatureNetwork.getOkHttpClientBuilder} for advanced configurations.
         * @param callAdapterFactory Determines how API responses are returned. For example, you can use
         *                           `RxJava3CallAdapterFactory.create()` to handle responses as RxJava observables.
         * @param clazz The API interface class that defines the various endpoints and methods available for
         *              interacting with the API.
         * @return An instance of the API service that can be used to make network requests.
         *
         * Usage:
         * val apiService = createAPI(
         *     baseUrl = "https://api.example.com/",
         *     okHttpClient = myOkHttpClient,
         *     callAdapterFactory = RxJava3CallAdapterFactory.create(),
         *     clazz = MyApiService::class.java
         * )
         *
         * @see KotlinFeatureNetwork.getOkHttpClientBuilder Configures OkHttpClient for network requests.
         */
        fun <T> createAPI(
            baseUrl: String,
            okHttpClient: OkHttpClient,
            callAdapterFactory: CallAdapter.Factory,
            clazz: Class<T>
        ): T {
            return Retrofit.Builder().baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(clazz)
        }
    }
}