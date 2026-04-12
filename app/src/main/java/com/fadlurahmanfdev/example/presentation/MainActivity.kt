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
import com.fadlurahmanfdev.example.data.api.JsonPlaceHolderAPI
import com.fadlurahmanfdev.example.data.dto.model.FeatureModel
import com.fadlurahmanfdev.example.data.repository.RepositoryDatasourceImpl
import com.fadlurahmanfdev.example.data.state.FetchNetworkState
import com.fadlurahmanfdev.example.domain.interceptor.ExampleHTTPFingerprintInterceptor
import com.fadlurahmanfdev.example.domain.interceptor.ExampleRetrySSLInterceptor
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
            enum = "FETCHED_POST_OK_USING_CORRECT_PINNING_PUBLIC_KEY"
        ),

        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post Not OK - Using Pinning Public Key",
            desc = "Fetched Post Not OK - Using Pinning Public Key",
            enum = "FETCHED_POST_NOT_OK_USING_INCORRECT_PINNING_PUBLIC_KEY"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post OK - Using Certificate From Resource",
            desc = "Fetched Post OK - Using Certificate From Resource",
            enum = "FETCHED_POST_OK_USING_CERT_FROM_RESOURCE"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post Not OK - Using Incorrect Fingerprint",
            desc = "Fetched Post Not OK - Using Incorrect Fingerprint",
            enum = "FETCHED_POST_NOT_OK_USING_INCORRECT_FINGERPRINT"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post OK - Using Fingerprint",
            desc = "Fetched Post OK - Using Fingerprint",
            enum = "FETCHED_POST_OK_USING_FINGERPRINT"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post Not OK - Using Incorrect Fingerprint",
            desc = "Fetched Post Not OK - Using Incorrect Fingerprint",
            enum = "FETCHED_POST_NOT_OK_USING_INCORRECT_FINGERPRINT"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Fetched Post - Retry Incorrect SSL Interceptor",
            desc = "Fetched Post - Retry Incorrect SSL Interceptor",
            enum = "FETCHED_POST_WITH_RETRY_INCORRECT_SSL"
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
                    jsonPlaceHolderAPIWithCorrectPinningPublicKey = jsonPlaceHolderAPIWithCorrectPinningPublicKey,
                    jsonPlaceHolderAPIWithIncorrectPinningPublicKey = jsonPlaceHolderAPIWithIncorrectPinningPublicKey,
                    jsonPlaceHolderAPIWithCorrectCertFromResource = jsonPlaceHolderAPIWithCorrectCertFromResource,
                    jsonPlaceHolderAPIWithIncorrectCertFromResource = jsonPlaceHolderAPIWithIncorrectCertFromResource,
                    jsonPlaceHolderAPIWithCorrectFingerprint = jsonPlaceHolderAPIWithCorrectFingerprint,
                    jsonPlaceHolderAPIWithIncorrectFingerprint = jsonPlaceHolderAPIWithIncorrectFingerprint,
                    jsonPlaceHolderAPIWithRetryMechanism = jsonPlaceHolderAPIRetrySSLMechanism,
                )
            )
        )

        rv.setItemViewCacheSize(features.size)
        rv.setHasFixedSize(true)

        adapter = ListExampleAdapter()
        adapter.setCallback(this)
        adapter.setList(features)
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

            "FETCHED_POST_OK_USING_CORRECT_PINNING_PUBLIC_KEY" -> {
                viewModel.fetchedPostUsingCorrectPinningPublicKey()
            }

            "FETCHED_POST_NOT_OK_USING_INCORRECT_PINNING_PUBLIC_KEY" -> {
                viewModel.fetchedPostUsingIncorrectPinningPublicKey()
            }

            "FETCHED_POST_OK_USING_CERT_FROM_RESOURCE" -> {
                viewModel.fetchedPostUsingCorrectCertFromResource()
            }

            "FETCHED_POST_NOT_OK_USING_INCORRECT_CERT_FROM_RESOURCE" -> {
                viewModel.fetchedPostUsingCorrectCertFromResource()
            }

            "FETCHED_POST_OK_USING_INCORRECT_FINGERPRINT" -> {
                viewModel.fetchedPostUsingCorrectFingerprint()
            }

            "FETCHED_POST_NOT_OK_USING_INCORRECT_FINGERPRINT" -> {
                viewModel.fetchedPostUsingIncorrectFingerprint()
            }

            "FETCHED_POST_WITH_RETRY_INCORRECT_SSL" -> {
                viewModel.fetchedPostWithRetrySSLMechanism()
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

    lateinit var jsonPlaceHolderAPIWithCorrectPinningPublicKey: JsonPlaceHolderAPI
    lateinit var jsonPlaceHolderAPIWithIncorrectPinningPublicKey: JsonPlaceHolderAPI
    lateinit var jsonPlaceHolderAPIWithCorrectCertFromResource: JsonPlaceHolderAPI
    lateinit var jsonPlaceHolderAPIWithIncorrectCertFromResource: JsonPlaceHolderAPI
    lateinit var jsonPlaceHolderAPIWithCorrectFingerprint: JsonPlaceHolderAPI
    lateinit var jsonPlaceHolderAPIWithIncorrectFingerprint: JsonPlaceHolderAPI
    lateinit var jsonPlaceHolderAPIRetrySSLMechanism: JsonPlaceHolderAPI
    private fun setupApiClient() {
        val networxAPI = NetworxAPI()
        val chuckerInterceptor = networxAPI.getChuckerInterceptorBuilder(this, true).build()
        val correctSSLPinner = networxAPI.getCertificatePinnerBuilder()
            .add(
                "jsonplaceholder.typicode.com",
                "sha256/IcwtGuxd2fA2t1B0ylJrjvtQm4g4vz5aVshokMHp2Qc=",
                "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=",
                "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c="
            )
            .build()
        val incorrectSSLPinner = networxAPI.getCertificatePinnerBuilder()
            .add(
                "jsonplaceholder.typicode.com",
                "sha256/B17MJoW6Bu9Hl+JStLT4gw+gm3nSDQ3lxuj6xKQrjmU=",
                "sha256/e0IRz5Tio3GA1Xs4fUVWmH1xHDiH2dMbVtCBSkOIdqM=",
                "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E="
            )
            .build()

        // Generate Trust Manager
        val correctTrustManager = this@MainActivity.networxAPI.getTrustManagerFromResource(
            context = applicationContext,
            alias = "jsonplaceholder-cert",
            certificateResource = R.raw.jsonplaceholder_cert
        )
        val incorrectTrustManager = this@MainActivity.networxAPI.getTrustManagerFromResource(
            context = applicationContext,
            alias = "jsonplaceholder-cert",
            certificateResource = R.raw.wikipedia_cert
        )
        val sslSocketFactory =
            this@MainActivity.networxAPI.getSslSocketFactory(correctTrustManager)
        val hostNameVerifier = HostnameVerifier { hostname, session ->
            Log.d(
                this::class.java.simpleName,
                "Example-Networx-LOG %%% - hostname: $hostname, session: ${session.isValid}"
            )
            Log.d(
                this::class.java.simpleName,
                "Example-Networx-LOG %%% - last accessed time: ${session.lastAccessedTime}, protocol: ${session.protocol}"
            )
            hostname == "jsonplaceholder.typicode.com"
        }

        val okHttpClientWithSSLPinnerBuilder = networxAPI.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            certificatePinner = correctSSLPinner
        ).addInterceptor(chuckerInterceptor)

        val okHttpClientWithCorrectCertFromResourceBuilder = networxAPI.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            sslSocketFactory = sslSocketFactory,
            x509TrustManager = correctTrustManager.filterIsInstance<X509TrustManager>()
                .firstOrNull(),
            hostnameVerifier = hostNameVerifier
        ).addInterceptor(chuckerInterceptor)

        val okHttpClientWithIncorrectCertFromResourceBuilder = networxAPI.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            sslSocketFactory = sslSocketFactory,
            x509TrustManager = incorrectTrustManager.filterIsInstance<X509TrustManager>()
                .firstOrNull(),
            hostnameVerifier = hostNameVerifier
        ).addInterceptor(chuckerInterceptor)

        val okHttpClientWithCorrectFingerprintBuilder = networxAPI.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
        ).addInterceptor(chuckerInterceptor)
        okHttpClientWithCorrectFingerprintBuilder.addInterceptor(
            ExampleHTTPFingerprintInterceptor(
                correct = true
            )
        )

        val okHttpClientWithIncorrectFingerprintBuilder = networxAPI.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
        ).addInterceptor(chuckerInterceptor)
        okHttpClientWithIncorrectFingerprintBuilder.addInterceptor(
            ExampleHTTPFingerprintInterceptor(
                correct = false
            )
        )

        val okHttpClientWithIncorrectSSLPinnerBuilder = networxAPI.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            certificatePinner = incorrectSSLPinner
        ).addInterceptor(chuckerInterceptor)

        val retryIncorrectSslOkHttpClientBuilder = networxAPI.getOkHttpClientBuilder(
            useLoggingInterceptor = true,
            certificatePinner = incorrectSSLPinner
        ).addInterceptor(chuckerInterceptor)
        retryIncorrectSslOkHttpClientBuilder.addInterceptor(
            ExampleRetrySSLInterceptor(
                this,
                retryIncorrectSslOkHttpClientBuilder.build(),
                networxAPI
            )
        )

        val okHttpClientWithCorrectSSLPinner = okHttpClientWithSSLPinnerBuilder.build()
        val okHttpClientWithIncorrectSSLPinner = okHttpClientWithIncorrectSSLPinnerBuilder.build()

        val okHttpClientWithCorrectCertFromResource =
            okHttpClientWithCorrectCertFromResourceBuilder.build()
        val okHttpClientWithIncorrectCertFromResource =
            okHttpClientWithIncorrectCertFromResourceBuilder.build()

        val okHttpClientWithCorrectFingerprint =
            okHttpClientWithCorrectFingerprintBuilder.build()
        val okHttpClientWithIncorrectFingerprint =
            okHttpClientWithIncorrectFingerprintBuilder.build()

        val retryIncorrectSslOkHttpClient = retryIncorrectSslOkHttpClientBuilder.build()

        jsonPlaceHolderAPIWithCorrectPinningPublicKey = networxAPI.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = okHttpClientWithCorrectSSLPinner,
            clazz = JsonPlaceHolderAPI::class.java
        )
        jsonPlaceHolderAPIWithIncorrectPinningPublicKey = networxAPI.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = okHttpClientWithIncorrectSSLPinner,
            clazz = JsonPlaceHolderAPI::class.java
        )

        jsonPlaceHolderAPIWithCorrectCertFromResource = networxAPI.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = okHttpClientWithCorrectCertFromResource,
            clazz = JsonPlaceHolderAPI::class.java
        )
        jsonPlaceHolderAPIWithIncorrectCertFromResource = networxAPI.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = okHttpClientWithIncorrectCertFromResource,
            clazz = JsonPlaceHolderAPI::class.java
        )

        jsonPlaceHolderAPIWithCorrectFingerprint = networxAPI.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = okHttpClientWithCorrectFingerprint,
            clazz = JsonPlaceHolderAPI::class.java
        )
        jsonPlaceHolderAPIWithIncorrectFingerprint = networxAPI.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = okHttpClientWithIncorrectFingerprint,
            clazz = JsonPlaceHolderAPI::class.java
        )

        jsonPlaceHolderAPIRetrySSLMechanism = networxAPI.createAPI(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            okHttpClient = retryIncorrectSslOkHttpClient,
            clazz = JsonPlaceHolderAPI::class.java
        )
    }
}