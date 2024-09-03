package com.fadlurahmanfdev.kotlin_feature_network.data.repository

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.fadlurahmanfdev.kotlin_feature_network.domain.plugin.KotlinFeatureNetwork
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class FeatureNetworkRepositoryImpl : FeatureNetworkRepository {
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
    override fun getCertificatePinnerBuilder(): CertificatePinner.Builder {
        return CertificatePinner.Builder()
    }

    override fun getTrustManagerFromResource(
        context: Context,
        certificateResource: Int,
        alias: String
    ): Array<TrustManager> {
        return KotlinFeatureNetwork.getTrustManagerFromResource(
            context = context,
            certificateResource = certificateResource,
            alias = alias
        )
    }

    override fun getTrustManager(x509TrustManager: X509TrustManager?): Array<TrustManager> {
        return KotlinFeatureNetwork.getTrustManager(x509TrustManager)
    }

    override fun getSslSocketFactory(trustManagers: Array<TrustManager>): SSLSocketFactory {
        return KotlinFeatureNetwork.getSslSocketFactory(trustManagers)
    }

    /**
     * The getOkHttpClientBuilder function provides a way to configure an OkHttpClient with optional logging and SSL certificate pinning. Adjusting these parameters customizes the client's behavior according to specific needs.
     * @param connectTimeout Sets the maximum time allowed for establishing a connection, in milliseconds. If null, the default timeout is used.
     * @param readTimeout Specifies the maximum time allowed for reading data from the server, in milliseconds. If null, the default timeout is applied.
     * @param writeTimeout Determines the maximum time allowed for writing data to the server, in milliseconds. If null, the default timeout is used.
     * @param useLoggingInterceptor Indicates whether to include a logging interceptor in the OkHttpClient. When set to true, network requests and responses will be logged, which is useful for debugging.
     * @param certificatePinner Allows the addition of an SSL certificate pinner to the OkHttpClient for extra security by pinning specific certificates. If set to null, no certificate pinning is applied.
     */
    override fun getOkHttpClientBuilder(
        connectTimeout: Long?,
        readTimeout: Long?,
        writeTimeout: Long?,
        useLoggingInterceptor: Boolean,
        certificatePinner: CertificatePinner?,
        sslSocketFactory: SSLSocketFactory?,
        x509TrustManager: X509TrustManager?
    ): OkHttpClient.Builder {
        return KotlinFeatureNetwork.getOkHttpClientBuilder(
            connectTimeout = connectTimeout,
            readTimeout = readTimeout,
            writeTimeout = writeTimeout,
            useLoggingInterceptor = useLoggingInterceptor,
            certificatePinner = certificatePinner,
            sslSocketFactory = sslSocketFactory,
            x509TrustManager = x509TrustManager
        )
    }

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
    override fun <T> createAPI(
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