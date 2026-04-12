# Overview

`networx` is a library designed to simplify and enhance network operations.

This library provides a suite of tools and methods to manage network requests efficiently, including
Dio client setup, logging, wifi feature, and SSL security.

## Key Features

- Generate Retrofit Client
- Network State Handler
- HTTP Inspector (via Chucker)
- SSL Handler (using Certificate From Resource, Pinning Public Key & SHA-256 Fingerprint)

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
val networxAPI = NetworxAPI()
val correctSSLPinner = networxAPI.getCertificatePinnerBuilder()
    .add(
        "jsonplaceholder.typicode.com",
        "sha256/IcwtGuxd2fA2t1B0ylJrjvtQm4g4vz5aVshokMHp2Qc=",
        "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=",
        "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c="
    )
    .build()

val okHttpClientWithSSLPinnerBuilder = networxAPI.getOkHttpClientBuilder(
    useLoggingInterceptor = true,
    certificatePinner = correctSSLPinner
)
val okHttpClientWithCorrectSSLPinner = okHttpClientWithSSLPinnerBuilder.build()

val jsonPlaceHolderAPIWithCorrectPinningPublicKey = networxAPI.createAPI(
    baseUrl = "https://jsonplaceholder.typicode.com/",
    okHttpClient = okHttpClientWithCorrectSSLPinner,
    clazz = JsonPlaceHolderAPI::class.java
)
```

Example of how to do SSL Pinning using Hardcoded Raw Resource Certificate:
```kotlin
val networxAPI = NetworxAPI()
// Generate trust manager from resource
val correctTrustManager = networxAPI.getTrustManagerFromResource(
    context = applicationContext,
    alias = "jsonplaceholder-cert",
    certificateResource = R.raw.jsonplaceholder_cert
)
// Generate SSL Socket from generataed trust manager
val sslSocketFactory = networxAPI.getSslSocketFactory(correctTrustManager)
// Generate HostNameVerifier
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

val okHttpClientWithCorrectCertFromResourceBuilder = networxAPI.getOkHttpClientBuilder(
    useLoggingInterceptor = true,
    sslSocketFactory = sslSocketFactory,
    x509TrustManager = correctTrustManager.filterIsInstance<X509TrustManager>()
        .firstOrNull(),
    hostnameVerifier = hostNameVerifier
)

val okHttpClientWithCorrectCertFromResource =
    okHttpClientWithCorrectCertFromResourceBuilder.build()

val jsonPlaceHolderAPIWithCorrectCertFromResource = networxAPI.createAPI(
    baseUrl = "https://jsonplaceholder.typicode.com/",
    okHttpClient = okHttpClientWithCorrectCertFromResource,
    clazz = JsonPlaceHolderAPI::class.java
)
```

Example of how to do HTTP Certificate Fingerprint:
```kotlin
val networxAPI = NetworxAPI()
val okHttpClientWithCorrectFingerprintBuilder = networxAPI.getOkHttpClientBuilder(
    useLoggingInterceptor = true,
)
okHttpClientWithCorrectFingerprintBuilder.addInterceptor(
    ExampleHTTPFingerprintInterceptor(
        correct = true
    )
)

val okHttpClientWithCorrectFingerprint =
    okHttpClientWithCorrectFingerprintBuilder.build()

val jsonPlaceHolderAPIWithCorrectFingerprint = networxAPI.createAPI(
    baseUrl = "https://jsonplaceholder.typicode.com/",
    okHttpClient = okHttpClientWithCorrectFingerprint,
    clazz = JsonPlaceHolderAPI::class.java
)
```

Example HTTP Fingerprint Interceptor:
```kotlin
class ExampleHTTPFingerprintInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        val headers: HashMap<String, String> = hashMapOf()
        request.headers.forEach { pair ->
            he
            aders[pair.first] = pair.second
        }
        // Get From Remote Config / Storage / Other
        val allowedFingerprint: List<String> = listOf<String>(
        "0a90b779d798ac916c9b9f04340bf2e9671be24777842b8502350763045fac8e"
        )

        val isUsingCorrectFingerprint = NetworxUtils.isUsingCorrectFingerprint(
            serverURL = url.toUrl().toString(),
            allowedFingerprints = allowedFingerprint,
            httpHeaderArgs = headers,
            timeout = 120,
            type = SHA.SHA256
        )

        Log.d(
            this::class.java.simpleName,
            "Example-Networx-LOG %%% isUsingCorrectFingerprint: $isUsingCorrectFingerprint"
        )

        if (!isUsingCorrectFingerprint) {
            throw SSLHandshakeException("CONNECTION_NOT_SECURE")
        }

        return chain.proceed(chain.request())
    }

}
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


