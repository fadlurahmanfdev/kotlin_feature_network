package com.fadlurahmanfdev.networx.base

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.fadlurahmanfdev.networx.data.repository.NetworxAPIRepository
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

open class BaseNetworxAPI : NetworxAPIRepository {
    /**
     * Creates a ChuckerInterceptor.Builder configured with the provided Context. This interceptor is used for inspecting and debugging HTTP requests and responses within the application.
     */
    override fun getChuckerInterceptorBuilder(context: Context): ChuckerInterceptor.Builder {
        val chuckerCollector = ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_DAY
        )

        return ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector)
            .maxContentLength(Long.MAX_VALUE)
            .alwaysReadResponseBody(true)
            .createShortcut(false)
    }

    /**
     * Generates a CertificatePinner.Builder, which is used to build a certificate pinner. This pinner ensures that only specified SSL certificates are accepted for secure connections, enhancing the security of network communications.
     */
    override fun getCertificatePinnerBuilder(): CertificatePinner.Builder =
        CertificatePinner.Builder()

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
     */
    override fun getOkHttpClientBuilder(
        connectTimeout: Long?,
        readTimeout: Long?,
        writeTimeout: Long?,
        useLoggingInterceptor: Boolean,
        certificatePinner: CertificatePinner?,
        sslSocketFactory: SSLSocketFactory?,
        x509TrustManager: X509TrustManager?,
        hostnameVerifier: HostnameVerifier?
    ): OkHttpClient.Builder = OkHttpClient.Builder().apply {
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

    /**
     * Creates and configures a Retrofit client for fetch HTTP Request.
     *
     * @param baseUrl HTTP URL of the API. For example, if baseUrl fill by "https://api.example.com/", it means all the request will begin wit this base url.
     * @param okHttpClient The HTTP client used to fetch API Request.
     * @param callAdapterFactory Determines how API responses are returned. For example, you can use
     *                           `RxJava3CallAdapterFactory.create()` to handle responses as RxJava observables.
     * @param converterFactory Determines how API can be converted when returned.
     *
     * Usage:
     * val retrofitBuilder = createRetrofit(
     *     baseUrl = "https://api.example.com/",
     *     okHttpClient = myOkHttpClient,
     *     callAdapterFactory = RxJava3CallAdapterFactory.create(),
     *     converterFactory = GsonConverterFactory.create(),
     * )
     *
     * @see getOkHttpClientBuilder Configures OkHttpClient for network requests.
     */
    override fun createRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
        converterFactory: Converter.Factory,
    ): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(converterFactory)
    }

    /**
     * Creates and configures a Retrofit API service for handling network requests.
     *
     * @param baseUrl HTTP URL of the API. For example, if baseUrl fill by "https://api.example.com/", it means all the request will begin wit this base url.
     * @param okHttpClient The HTTP client used to fetch API Request.
     * @param callAdapterFactory Determines how API responses are returned. For example, you can use
     *                           `RxJava3CallAdapterFactory.create()` to handle responses as RxJava observables.
     * @param converterFactory Determines how API can be converted when returned.
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
     *     converterFactory = GsonConverterFactory.create(),
     *     clazz = MyApiService::class.java
     * )
     *
     * @see createRetrofit configure retrofit to create an API Client.
     */
    override fun <T> createAPI(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
        converterFactory: Converter.Factory,
        clazz: Class<T>,
    ): T {
        return createRetrofit(
            baseUrl = baseUrl,
            okHttpClient = okHttpClient,
            callAdapterFactory = callAdapterFactory,
            converterFactory = converterFactory,
        ).build().create(clazz)
    }
}