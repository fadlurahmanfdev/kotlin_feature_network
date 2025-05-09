package com.fadlurahmanfdev.networx.base

import android.content.Context
import androidx.annotation.RawRes
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
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

open class BaseNetworxAPI : NetworxAPIRepository {
    /**
     * Creates a ChuckerInterceptor.Builder configured with the provided Context. This interceptor is used for inspecting and debugging HTTP requests and responses within the application.
     */
    override fun getChuckerInterceptorBuilder(context: Context, showNotification: Boolean): ChuckerInterceptor.Builder {
        val chuckerCollector = ChuckerCollector(
            context = context,
            showNotification = showNotification,
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
    override fun getTrustManagerFromResource(
        context: Context,
        @RawRes certificateResource: Int,
        alias: String
    ): Array<TrustManager> {
        val certFactory = CertificateFactory.getInstance("X.509")
        val certInputStream: InputStream = context.resources.openRawResource(certificateResource)
        val cert = certFactory.generateCertificate(certInputStream) as X509Certificate
        certInputStream.close()

        // Create a KeyStore and load the certificate
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null)
        keyStore.setCertificateEntry(alias, cert)

        // Initialize TrustManagerFactory with the KeyStore
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(keyStore)

        // Return the first X509TrustManager
        val trustManagers = tmf.trustManagers
        return trustManagers
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
     * @see getTrustManagerFromResource Retrieves TrustManagers from a certificate resource.
     */
    override fun getSslSocketFactory(trustManagers: Array<TrustManager>): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, null)
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