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
import com.fadlurahmanfdev.example.data.api.BankMasIdentityAPI
import com.fadlurahmanfdev.example.data.dto.model.FeatureModel
import com.fadlurahmanfdev.example.data.repository.RepositoryDatasourceImpl
import com.fadlurahmanfdev.example.domain.ExampleNetworkUseCaseImpl
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {
    lateinit var viewModel: MainViewModel

    private val features: List<FeatureModel> = listOf<FeatureModel>(
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Generate Guest Token",
            desc = "Generate Guest Token",
            enum = "GENERATE_GUEST_TOKEN"
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

        val networkRepository: FeatureNetworkRepository = FeatureNetworkRepositoryImpl()

        viewModel = MainViewModel(
            exampleNetworkUseCase = ExampleNetworkUseCaseImpl(
                repositoryDatasourceImpl = RepositoryDatasourceImpl(
                    identityBankMasApi = networkRepository.createAPI(
                        baseUrl = "https://api.bankmas.my.id/",
                        okHttpClient = networkRepository.getOkHttpClientBuilder(
                            useLoggingInterceptor = true,
                            sslCertificatePinner = networkRepository.getCertificatePinnerBuilder()
                                .add(
                                    "api.bankmas.my.id",
                                    "sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI",
                                    "sha256/18tkPyr2nckv4fgo0dhAkaUtJ2hu2831xlO2SKhq8dg",
                                    "sha256/QlV/1sAaZ+gEZYeNctG/Y2nUSEiafHQB3z35kAFSIxU"
                                )
                                .build()
                        )
                            .addInterceptor(
                                networkRepository.getChuckerInterceptorBuilder(this).build()
                            )
                            .build(),
                        callAdapterFactory = RxJava3CallAdapterFactory.create(),
                        clazz = BankMasIdentityAPI::class.java
                    )
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
    }

    override fun onClicked(item: FeatureModel) {
        when (item.enum) {
            "GENERATE_GUEST_TOKEN" -> {
                viewModel.generateGuestSession()
            }
        }
    }
}