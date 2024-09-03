package com.fadlurahmanfdev.kotlin_feature_network.data.repository

import android.content.Context
import androidx.annotation.RawRes
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface FeatureNetworkRepository {
    /**
     * Creates a ChuckerInterceptor.Builder configured with the provided Context. This interceptor is used for inspecting and debugging HTTP requests and responses within the application.
     */
    fun getChuckerInterceptorBuilder(context: Context): ChuckerInterceptor.Builder

    /**
     * Generates a CertificatePinner.Builder, which is used to build a certificate pinner. This pinner ensures that only specified SSL certificates are accepted for secure connections, enhancing the security of network communications.
     */
    fun getCertificatePinnerBuilder(): CertificatePinner.Builder

    fun getTrustManagerFromResource(
        context: Context,
        @RawRes certificateResource: Int,
        alias: String,
    ): Array<TrustManager>

    fun getTrustManager(x509TrustManager: X509TrustManager? = null): Array<TrustManager>

    fun getSslSocketFactory(trustManagers: Array<TrustManager>): SSLSocketFactory

    /**
     * The getOkHttpClientBuilder function provides a way to configure an OkHttpClient with optional logging and SSL certificate pinning. Adjusting these parameters customizes the client's behavior according to specific needs.
     * @param useLoggingInterceptor Indicates whether to include a logging interceptor in the OkHttpClient. When set to true, network requests and responses will be logged, which is useful for debugging.
     * @param certificatePinner Allows the addition of an SSL certificate pinner to the OkHttpClient for extra security by pinning specific certificates. If set to null, no certificate pinning is applied.
     */
    fun getOkHttpClientBuilder(
        connectTimeout: Long? = null,
        readTimeout: Long? = null,
        writeTimeout: Long? = null,
        useLoggingInterceptor: Boolean = false,
        certificatePinner: CertificatePinner? = null,
        sslSocketFactory: SSLSocketFactory? = null,
        x509TrustManager: X509TrustManager? = null,
        hostnameVerifier: HostnameVerifier? = null,
    ): OkHttpClient.Builder

    /**
     * The createAPI function efficiently sets up an API service.
     * By providing the base URL, network connection settings, response format, and API interface, a fully functional service for handling network requests is created.
     * @param baseUrl Specifies the main URL for the API. All requests will be directed to this address.
     * For example, setting it to "https://api.example.com/" means that any requests made will begin with this URL.
     * @param okHttpClient Handles the network connection. Customization options include adding timeouts or logging network calls.
     * @param callAdapterFactory Determines how API responses are returned, such as in a simple object format or as an RxJava Observable.
     * For example, setting it to RxJava3CallAdapterFactory.create().
     * @param clazz Defines the API interface, specifying the various endpoints (actions) that can be performed within the code.
     * */
    fun <T> createAPI(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
        clazz: Class<T>
    ): T
}