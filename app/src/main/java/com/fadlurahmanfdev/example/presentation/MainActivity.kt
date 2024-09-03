package com.fadlurahmanfdev.example.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.kotlin_feature_network.data.repository.FeatureNetworkRepository
import com.fadlurahmanfdev.kotlin_feature_network.data.repository.FeatureNetworkRepositoryImpl
import com.fadlurahmanfdev.example.data.api.JsonPlaceHolderAPI
import com.fadlurahmanfdev.example.data.dto.model.FeatureModel
import com.fadlurahmanfdev.example.data.repository.RepositoryDatasourceImpl
import com.fadlurahmanfdev.example.data.state.FetchNetworkState
import com.fadlurahmanfdev.example.domain.interceptor.ExampleSSLInterceptor
import com.fadlurahmanfdev.example.domain.usecase.ExampleNetworkUseCaseImpl
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {
    lateinit var viewModel: MainViewModel

    private val features: List<FeatureModel> = listOf<FeatureModel>(
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post",
            desc = "Fetched Post - OK",
            enum = "FETCHED_POST_OK"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post",
            desc = "Fetched Post - Incorrect SSL",
            enum = "FETCHED_POST_INCORRECT_SSL"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post",
            desc = "Fetched Post - Retry Incorrect SSL",
            enum = "FETCHED_POST_RETRY_INCORRECT_SSL"
        ),
    )

    private lateinit var rv: RecyclerView

    private lateinit var adapter: ListExampleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rv = findViewById<RecyclerView>(R.id.rv)

        setupApiClient()

        viewModel = MainViewModel(
            exampleNetworkUseCase = ExampleNetworkUseCaseImpl(
                repositoryDatasource = RepositoryDatasourceImpl(
                    jsonPlaceHolderAPI = jsonPlaceHolderAPI,
                    jsonPlaceHolderIncorrectSslAPI = jsonPlaceHolderIncorrectSSLAPI,
                    jsonPlaceHolderRetryIncorrectSslAPI = jsonPlaceHolderRetryIncorrectSSLAPI
                )
            )
        )

        rv.setItemViewCacheSize(features.size)
        rv.setHasFixedSize(true)

        adapter = ListExampleAdapter()
        adapter.setCallback(this)
        adapter.setList(features)
        adapter.setHasStableIds(true)
        rv.adapter = adapter

        viewModel.fetchedPostState.observe(this) { state ->
            when (state) {
                is FetchNetworkState.FAILED -> {
                    dismissInfoBottomsheet()
                    showInfoBottomsheet(state.title, state.message)
                }

                FetchNetworkState.LOADING -> {
                    showLoading()
                }

                FetchNetworkState.SUCCESS -> {
                    dismissLoading()
                    showInfoBottomsheet("Success / Sukses", "OK")
                }

                else -> {

                }
            }
        }
    }

    override fun onClicked(item: FeatureModel) {
        when (item.enum) {
            "FETCHED_POST_OK" -> {
                viewModel.fetchedPostOk()
            }

            "FETCHED_POST_INCORRECT_SSL" -> {
                viewModel.fetchedPostIncorrectSsl()
            }

            "FETCHED_POST_RETRY_INCORRECT_SSL" -> {
                viewModel.fetchedPostRetryIncorrectSsl()
            }
        }
    }

    var loadingDialog: LoadingDialog? = null
    fun showLoading() {
        dismissLoading()
        loadingDialog = LoadingDialog()
        loadingDialog?.show(supportFragmentManager, LoadingDialog::class.java.simpleName)
    }

    fun dismissLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    var infoBottomsheet: InfoBottomsheet? = null
    fun showInfoBottomsheet(title: String, desc: String) {
        dismissLoading()
        dismissInfoBottomsheet()

        infoBottomsheet = InfoBottomsheet.newInstance(title, desc)
        infoBottomsheet?.show(supportFragmentManager, InfoBottomsheet::class.java.simpleName)
    }

    fun dismissInfoBottomsheet() {
        infoBottomsheet?.dismiss()
        infoBottomsheet = null
    }

    lateinit var jsonPlaceHolderAPI: JsonPlaceHolderAPI
    lateinit var jsonPlaceHolderIncorrectSSLAPI: JsonPlaceHolderAPI
    lateinit var jsonPlaceHolderRetryIncorrectSSLAPI: JsonPlaceHolderAPI
    private fun setupApiClient() {
        val networkRepository: FeatureNetworkRepository = FeatureNetworkRepositoryImpl()
        val chuckerInterceptor = networkRepository.getChuckerInterceptorBuilder(this).build()
        val jsonPlaceHolderIncorrectSslPinner = networkRepository.getCertificatePinnerBuilder()
            .add(
                "jsonplaceholder.typicode.com",
                "sha256/B17MJoW6Bu9Hl+JStLT4gw+gm3nSDQ3lxuj6xKQrjmU=",
                "sha256/e0IRz5Tio3GA1Xs4fUVWmH1xHDiH2dMbVtCBSkOIdqM=",
                "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E="
            )
            .build()
        val jsonPlaceHolderSslPinner = networkRepository.getCertificatePinnerBuilder()
            .add(
                "jsonplaceholder.typicode.com",
                "sha256/IcwtGuxd2fA2t1B0ylJrjvtQm4g4vz5aVshokMHp2Qc=",
                "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=",
                "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c="
            )
            .build()
        val okHttpClient = networkRepository.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            sslCertificatePinner = jsonPlaceHolderSslPinner
        ).addInterceptor(chuckerInterceptor).build()
        val retryIncorrectSslOkHttpClientBuilder = networkRepository.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            sslCertificatePinner = jsonPlaceHolderIncorrectSslPinner
        ).addInterceptor(chuckerInterceptor)
        retryIncorrectSslOkHttpClientBuilder.addInterceptor(
            ExampleSSLInterceptor(
                this,
                retryIncorrectSslOkHttpClientBuilder.build(),
                networkRepository
            )
        )
        val retryIncorrectSslOkHttpClient = retryIncorrectSslOkHttpClientBuilder.build()
        val incorrectSslOkHttpClientBuilder = networkRepository.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            sslCertificatePinner = jsonPlaceHolderIncorrectSslPinner
        ).addInterceptor(chuckerInterceptor)
        val incorrectSslOkHttpClient = incorrectSslOkHttpClientBuilder.build()
        jsonPlaceHolderAPI = networkRepository.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = okHttpClient,
            callAdapterFactory = RxJava3CallAdapterFactory.create(),
            clazz = JsonPlaceHolderAPI::class.java
        )
        jsonPlaceHolderIncorrectSSLAPI = networkRepository.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = incorrectSslOkHttpClient,
            callAdapterFactory = RxJava3CallAdapterFactory.create(),
            clazz = JsonPlaceHolderAPI::class.java
        )
        jsonPlaceHolderRetryIncorrectSSLAPI = networkRepository.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = retryIncorrectSslOkHttpClient,
            callAdapterFactory = RxJava3CallAdapterFactory.create(),
            clazz = JsonPlaceHolderAPI::class.java
        )
    }
}