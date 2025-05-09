package com.fadlurahmanfdev.example.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.networx.data.repository.NetworxAPIRepository
import com.fadlurahmanfdev.example.data.api.JsonPlaceHolderAPI
import com.fadlurahmanfdev.example.data.dto.model.FeatureModel
import com.fadlurahmanfdev.example.data.repository.RepositoryDatasourceImpl
import com.fadlurahmanfdev.example.data.state.FetchNetworkState
import com.fadlurahmanfdev.example.domain.interceptor.ExampleNetworxSSLInterceptor
import com.fadlurahmanfdev.example.domain.usecase.ExampleNetworkUseCaseImpl
import com.fadlurahmanfdev.example.presentation.adapter.ListExampleAdapter
import com.fadlurahmanfdev.networx.NetworxWifi
import com.fadlurahmanfdev.networx.NetworxAPI
import com.fadlurahmanfdev.networx.NetworxManager
import com.fadlurahmanfdev.networx.base.BaseNetworxAPI
import com.fadlurahmanfdev.networx.data.enum.NetworkType
import com.fadlurahmanfdev.networx.data.repository.NetworxStateListener
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.X509TrustManager

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {
    lateinit var viewModel: MainViewModel

    @OptIn(ExperimentalStdlibApi::class)
    private lateinit var featureWifi: NetworxWifi
    private lateinit var networxAPI: BaseNetworxAPI
    private lateinit var networxManager: NetworxManager

    private val features: List<FeatureModel> = listOf<FeatureModel>(
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Check whether connected to network",
            desc = "Check whether connected to network",
            enum = "IS_CONNECTED_TO_NETWORK"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Check whether connected to wifi",
            desc = "Check whether connected to wifi",
            enum = "IS_CONNECTED_TO_WIFI"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Check whether connected to cellular",
            desc = "Check whether connected to cellular",
            enum = "IS_CONNECTED_TO_CELLULAR"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Check whether connected to ethernet",
            desc = "Check whether connected to ethernet",
            enum = "IS_CONNECTED_TO_ETHERNET"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Listen Connectivity Change",
            desc = "Listen Connectivity Change",
            enum = "LISTEN_CONNECTIVITY_CHANGE"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Remove Listen Connectivity Change",
            desc = "Remove Listen Connectivity Change",
            enum = "REMOVE_LISTEN_CONNECTIVITY_CHANGE"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "--- API REQUEST ---",
            desc = "--------------------------------------------------",
            enum = "DIVIDER-API-REQUEST"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post OK - Using Pinning Public Key",
            desc = "Fetched Post OK - Using Pinning Public Key",
            enum = "FETCHED_POST_OK_USING_PINNING_PUBLIC_KEY"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post OK - Using Raw Res Pem",
            desc = "Fetched Post OK - Using Raw Res Pem",
            enum = "FETCHED_POST_OK_USING_RAW_RES_PEM"
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
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "--- WIFI IMPLEMENTATION ---",
            desc = "--------------------------------------------------",
            enum = "DIVIDER-WIFI"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Scan Nearby Wifi",
            desc = "Scan Nearby Wifi",
            enum = "SCAN_NEARBY_WIFI"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "--- P2P IMPLEMENTATION ---",
            desc = "--------------------------------------------------",
            enum = "DIVIDER-P2P"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Discover P2P Device",
            desc = "Discover P2P Device",
            enum = "DISCOVER_P2P_DEVICE"
        ),
    )

    private lateinit var rv: RecyclerView

    private lateinit var adapter: ListExampleAdapter

    @OptIn(ExperimentalStdlibApi::class)
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

        featureWifi = NetworxWifi(applicationContext)
        networxAPI = NetworxAPI()
        networxManager = NetworxManager(this)

        setupApiClient()

        viewModel = MainViewModel(
            exampleNetworkUseCase = ExampleNetworkUseCaseImpl(
                repositoryDatasource = RepositoryDatasourceImpl(
                    jsonPlaceHolderAPI = jsonPlaceHolderAPI,
                    jsonPlaceHolderUsingRawResPem = jsonPlaceHolderRawResPemAPI,
                    jsonPlaceHolderIncorrectSslAPI = jsonPlaceHolderIncorrectSSLAPI,
                    jsonPlaceHolderRetryIncorrectSslAPI = jsonPlaceHolderRetryIncorrectSSLAPI,
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
            "IS_CONNECTED_TO_NETWORK" -> {
                val isConnected = networxManager.isConnected()
                Log.d(
                    this::class.java.simpleName,
                    "Example-Networx-LOG %%% is connected: $isConnected"
                )
            }

            "IS_CONNECTED_TO_WIFI" -> {
                val isConnected = networxManager.isConnectedToWifi()
                Log.d(
                    this::class.java.simpleName,
                    "Example-Networx-LOG %%% is connected to wifi: $isConnected"
                )
            }

            "IS_CONNECTED_TO_CELLULAR" -> {
                val isConnected = networxManager.isConnectedToCellular()
                Log.d(
                    this::class.java.simpleName,
                    "Example-Networx-LOG %%% is connected to cellular: $isConnected"
                )
            }

            "IS_CONNECTED_TO_ETHERNET" -> {
                val isConnected = networxManager.isConnectedToEthernet()
                Log.d(
                    this::class.java.simpleName,
                    "Example-Networx-LOG %%% is connected to ethernet: $isConnected"
                )
            }

            "FETCHED_POST_OK_USING_PINNING_PUBLIC_KEY" -> {
                viewModel.fetchedPostOkPinningPublicKey()
            }

            "FETCHED_POST_OK_USING_RAW_RES_PEM" -> {
                viewModel.fetchedPostOkPinningRawResPem()
            }

            "FETCHED_POST_INCORRECT_SSL" -> {
                viewModel.fetchedPostIncorrectSsl()
            }

            "FETCHED_POST_RETRY_INCORRECT_SSL" -> {
                viewModel.fetchedPostRetryIncorrectSsl()
            }

            "LISTEN_CONNECTIVITY_CHANGE" -> {
                networxManager.listenNetworkState(this, object : NetworxStateListener {
                    override fun onConnectedNetworkTypeChange(type: NetworkType) {
                        Log.d(
                            this@MainActivity::class.java.simpleName,
                            "EXAMPLE-Networx-LOG %%% on connected network type changed into $type"
                        )
                    }

                    override fun onConnectedChange(connected: Boolean, networkType: NetworkType?) {
                        Log.d(
                            this@MainActivity::class.java.simpleName,
                            "EXAMPLE-Networx-LOG %%% on connected change: $connected, connected into: $networkType"
                        )
                    }

                })
            }

            "REMOVE_LISTEN_CONNECTIVITY_CHANGE" -> {
                networxManager.removeListenerNetworkState(this)
            }

            "SCAN_NEARBY_WIFI" -> {
                val intent = Intent(this, ScanWifiActivity::class.java)
                startActivity(intent)
//                featureWifi.scanNearbyWifi(this, object : KotlinFeatureWifi.ScanWifiCallback {
//                    override fun onSuccessScanNearbyWifi(wifiResults: List<FeatureWifiInfoModel>) {
//                        wifiResults.forEach { wifi ->
//                            Log.d(this@MainActivity::class.java.simpleName, "wifi: ${wifi.name}, ${wifi.address}")
//                        }
//                    }
//
//                    override fun onFailedScanNearbyWifi(exception: FeatureNetworkException) {
//                        Log.e(this@MainActivity::class.java.simpleName, "failed scan nearby wifi: $exception")
//                    }
//                })
            }

            "DISCOVER_P2P_DEVICE" -> {
                val intent = Intent(this, ScanNearbyP2PDeviceActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
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
    lateinit var jsonPlaceHolderRawResPemAPI: JsonPlaceHolderAPI
    private fun setupApiClient() {
        val networkRepository: NetworxAPIRepository = NetworxAPI()
        val chuckerInterceptor = networkRepository.getChuckerInterceptorBuilder(this, true).build()
        val jsonPlaceHolderSslPinner = networkRepository.getCertificatePinnerBuilder()
            .add(
                "jsonplaceholder.typicode.com",
                "sha256/IcwtGuxd2fA2t1B0ylJrjvtQm4g4vz5aVshokMHp2Qc=",
                "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=",
                "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c="
            )
            .build()
        val jsonPlaceHolderIncorrectSslPinner = networkRepository.getCertificatePinnerBuilder()
            .add(
                "jsonplaceholder.typicode.com",
                "sha256/B17MJoW6Bu9Hl+JStLT4gw+gm3nSDQ3lxuj6xKQrjmU=",
                "sha256/e0IRz5Tio3GA1Xs4fUVWmH1xHDiH2dMbVtCBSkOIdqM=",
                "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E="
            )
            .build()

        // Generate Trust Manager
        val jsonPlaceholderTrustManager = networxAPI.getTrustManagerFromResource(
            context = applicationContext,
            alias = "jsonplaceholder-cert",
            certificateResource = R.raw.jsonplaceholder_cert
        )
        val sslSocketFactory = networxAPI.getSslSocketFactory(jsonPlaceholderTrustManager)
        val hostNameVerifier = HostnameVerifier { hostname, session ->
            Log.d(this::class.java.simpleName, "Example-Networx-LOG %%% - hostname: $hostname, session: ${session.isValid}")
            Log.d(this::class.java.simpleName, "Example-Networx-LOG %%% - last accessed time: ${session.lastAccessedTime}, protocol: ${session.protocol}")
            hostname == "jsonplaceholder.typicode.com"
        }

        val okHttpClientBuilder = networkRepository.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            certificatePinner = jsonPlaceHolderSslPinner
        ).addInterceptor(chuckerInterceptor)
        val okHttpClientRawResPemBuilder = networkRepository.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            sslSocketFactory = sslSocketFactory,
            x509TrustManager = jsonPlaceholderTrustManager.filterIsInstance<X509TrustManager>()
                .firstOrNull(),
            hostnameVerifier = hostNameVerifier
        ).addInterceptor(chuckerInterceptor)
        val incorrectSslOkHttpClientBuilder = networkRepository.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            certificatePinner = jsonPlaceHolderIncorrectSslPinner
        ).addInterceptor(chuckerInterceptor)
        val retryIncorrectSslOkHttpClientBuilder = networkRepository.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            certificatePinner = jsonPlaceHolderIncorrectSslPinner
        ).addInterceptor(chuckerInterceptor)
        retryIncorrectSslOkHttpClientBuilder.addInterceptor(
            ExampleNetworxSSLInterceptor(
                this,
                retryIncorrectSslOkHttpClientBuilder.build(),
                networkRepository
            )
        )

        val okHttpClient= okHttpClientBuilder.build()
        val okHttpClientRawResPem= okHttpClientRawResPemBuilder.build()
        val incorrectSslOkHttpClient = incorrectSslOkHttpClientBuilder.build()
        val retryIncorrectSslOkHttpClient = retryIncorrectSslOkHttpClientBuilder.build()
        jsonPlaceHolderAPI = networkRepository.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = okHttpClient,
            clazz = JsonPlaceHolderAPI::class.java
        )
        jsonPlaceHolderRawResPemAPI = networkRepository.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = okHttpClientRawResPem,
            clazz = JsonPlaceHolderAPI::class.java
        )
        jsonPlaceHolderIncorrectSSLAPI = networkRepository.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = incorrectSslOkHttpClient,
            clazz = JsonPlaceHolderAPI::class.java
        )
        jsonPlaceHolderRetryIncorrectSSLAPI = networkRepository.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = retryIncorrectSslOkHttpClient,
            clazz = JsonPlaceHolderAPI::class.java
        )
    }
}