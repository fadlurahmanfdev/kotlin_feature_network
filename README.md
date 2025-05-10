# Overview

`networx` is a library designed to simplify and enhance network operations.

This library provides a suite of tools and methods to manage network requests efficiently, including
Dio client setup, logging, wifi feature, and SSL security.

## Key Features

- Generate Retrofit Client
- Network State Handler
- HTTP Inspector (via Chucker)
- SSL Handler

## Networx Manager

### Network State

Check whether device is connected through some network.

```kotlin
var networxManager: NetworxManager = NetworxManager(applicationContext)

// Check whether device is connected through internet
val isConnected = networxManager.isConnected()

// Check whether device is connected through wifi
val isConnected = networxManager.isConnectedToWifi()

// Check whether device is connected through mobile cellular
val isConnected = networxManager.isConnectedToCellular()

// Check whether device is connected through ethernet
val isConnected = networxManager.isConnectedToEthernet()
```

## API Request

### Chucker - HTTP Inspector

Dev tools to help whether the api request is work successfully.

Big Thanks to [Chucker](https://github.com/ChuckerTeam/chucker?tab=readme-ov-file#chucker) Library.

```kotlin
val networkRepository: NetworxAPIRepository = NetworxAPI()
val chuckerInterceptor = networkRepository.getChuckerInterceptorBuilder(this, true).build()
val okHttpClient = networkRepository.getOkHttpClientBuilder(
    useLoggingInterceptor = true,
).addInterceptor(chuckerInterceptor).build()
val jsonPlaceHolderAPI = networkRepository.createAPI(
    baseUrl = "https://jsonplaceholder.typicode.com/",
    okHttpClient = okHttpClient,
    clazz = JsonPlaceHolderAPI::class.java
)
```

<p float="left">
  <img src="https://raw.githubusercontent.com/fadlurahmanfdev/kotlin_feature_network/master/media/chucker-example-1.png" width="49%" />
  <img src="https://raw.githubusercontent.com/fadlurahmanfdev/kotlin_feature_network/master/media/chucker-example-2.png" width="49%" />
</p>

### SSL Pinning

SSL or Socket Secure Layer Pinning is a security technique to trust only hardcode certificate or public key. 

Example of how to do SSL Pinning using Hardcoded Public Key:
```kotlin
val networkRepository: NetworxAPIRepository = NetworxAPI()
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
    certificatePinner = jsonPlaceHolderSslPinner
).build()
val jsonPlaceHolderAPI = networkRepository.createAPI(
    baseUrl = "https://jsonplaceholder.typicode.com/",
    okHttpClient = okHttpClient,
    clazz = JsonPlaceHolderAPI::class.java
)
```

Example of how to do SSL Pinning using Hardcoded Raw Resource Certificate:
```kotlin
// Generate trust manager from resource
val jsonPlaceholderTrustManager = networxAPI.getTrustManagerFromResource(
            context = applicationContext,
            alias = "jsonplaceholder-cert",
            certificateResource = R.raw.jsonplaceholder_cert
        )
// Generate SSL Socket from generataed trust manager
val sslSocketFactory = networxAPI.getSslSocketFactory(jsonPlaceholderTrustManager)
// Generate HostNameVerifier
val hostNameVerifier = HostnameVerifier { hostname, session ->
    hostname == "jsonplaceholder.typicode.com"
}
val okHttpClientRawResPem = networkRepository.getOkHttpClientBuilder(
    useLoggingInterceptor = true,
    sslSocketFactory = sslSocketFactory,
    x509TrustManager = jsonPlaceholderTrustManager.filterIsInstance<X509TrustManager>()
        .firstOrNull(),
    hostnameVerifier = hostNameVerifier
).addInterceptor(chuckerInterceptor).build()
val jsonPlaceHolderRawResPemAPI = networkRepository.createAPI(
    baseUrl = "https://jsonplaceholder.typicode.com/",
    okHttpClient = okHttpClientRawResPem,
    clazz = JsonPlaceHolderAPI::class.java
)
```

## Wifi Implementation

### Scan Nearby Wifi

Scan nearby wifi using Wifi Manager.

```kotlin
var networxWifi: NetworxWifi = NetworxWifi(applicationContext)
networxWifi.scanNearbyWifi(this, object : NetworxWifi.ScanWifiCallback {
    override fun onSuccessScanNearbyWifi(wifiResults: List<FeatureWifiInfoModel>) {
        // on success scan nearby wifi
    }

    override fun onFailedScanNearbyWifi(exception: NetworxException) {
        // on failed scan nearby wifi
    }
})
```

### Stop scan nearby wifi

After get result from scan nearby wifi, better to stop scan to avoid battery drainage.

```kotlin
var networxWifi: NetworxWifi = NetworxWifi(applicationContext)
networxWifi.stopScanNearbyWifi(this)
```

## Coming Soon Feature

- Authenticator For Retryable Request



