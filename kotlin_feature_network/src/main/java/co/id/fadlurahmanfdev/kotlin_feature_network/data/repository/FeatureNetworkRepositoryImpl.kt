package co.id.fadlurahmanfdev.kotlin_feature_network.data.repository

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FeatureNetworkRepositoryImpl : FeatureNetworkRepository {
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

    override fun getCertificatePinnerBuilder(): CertificatePinner.Builder {
        return CertificatePinner.Builder()
    }

    override fun getOkHttpClientBuilder(
        useLoggingInterceptor: Boolean,
        sslCertificatePinner: CertificatePinner?
    ): OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            if (sslCertificatePinner != null) {
                certificatePinner(sslCertificatePinner)
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
     * @param callAdapterFactory put RxJava3CallAdapterFactory.create()
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