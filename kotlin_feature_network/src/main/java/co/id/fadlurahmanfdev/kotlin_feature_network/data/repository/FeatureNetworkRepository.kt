package co.id.fadlurahmanfdev.kotlin_feature_network.data.repository

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.CallAdapter

interface FeatureNetworkRepository {
    fun getChuckerInterceptorBuilder(context: Context): ChuckerInterceptor.Builder
    fun getCertificatePinnerBuilder(): CertificatePinner.Builder

    fun getOkHttpClientBuilder(
        useLoggingInterceptor: Boolean = true,
        sslCertificatePinner: CertificatePinner? = null
    ): OkHttpClient.Builder

    /**
     * @param callAdapterFactory put RxJava3CallAdapterFactory.create()
     * */
    fun <T> createAPI(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
        clazz: Class<T>
    ): T
}