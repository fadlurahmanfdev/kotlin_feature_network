# Overview

`kotlin_feature_network` is a library designed to simplify and enhance network operations in
Android applications.
This library provides a suite of tools and methods to manage network requests efficiently, including
Dio client setup, logging, request identification, and SSL security.

## Methods

#### Get TrustManager - From Resource @RawRes

Retrieves an array of TrustManagers that trusts the certificate provided in the resources.

```kotlin
val trustManagers = getTrustManagerFromResource(context, R.raw.my_certificate, "myAlias")
```

| Parameter Name        | Type     | Required | Description                                                                             |
|-----------------------|----------|----------|-----------------------------------------------------------------------------------------|
| `context`             | Context  | yes      | The application context used to access the raw resources.                               |
| `certificateResource` | @RawRes  | yes      | The resource ID of the certificate (in .crt or .pem format) stored in the 'raw' folder. |
| `alias`               | String   | yes      | A unique alias used to identify the certificate in the KeyStore.                        |

#### Get TrustManager

Retrieves an array of TrustManagers that trusts the certificate provided in the resources.

```kotlin
val trustManagers = getTrustManager() // Uses default TrustManager.
// or
val trustManagers = getTrustManager(customTrustManager) // Uses a custom TrustManager.
```

| Parameter Name      | Type             | Required | Description                                                                                                                                                            |
|---------------------|------------------|----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `x509TrustManager`  | X509TrustManager | no       | An optional custom X509TrustManager that can be passed to manage certificate trust. If null, a default implementation is provided which only logs certificate details. |

#### Get SSL Socket Factory

This function generates an SSLSocketFactory based on the array of TrustManagers passed as a parameter.

```kotlin
val trustManagers = getTrustManagerFromResource(context, R.raw.my_certificate, "myAlias") 
val sslSocketFactory = getSslSocketFactory(trustManagers)
```

| Parameter Name   | Type                | Required | Description                                                    |
|------------------|---------------------|----------|----------------------------------------------------------------|
| `trustManagers`  | Array<TrustManager> | yes      | The array of TrustManagers used to validate SSL certificates.  |


#### Get OK HTTP Client Builder

This function configures an OkHttpClient.Builder with specified timeout settings, optional logging,
and SSL certificate pinning, offering flexibility for network operations.

```kotlin
val clientBuilder = getOkHttpClientBuilder(
    onnectTimeout = 10000L,
    readTimeout = 10000L,
    writeTimeout = 10000L,
    useLoggingInterceptor = true,
    certificatePinner = myCertificatePinner,
    sslSocketFactory = mySslSocketFactory,
    x509TrustManager = myTrustManager,
    hostnameVerifier = myHostnameVerifier
)
```

| Parameter Name            | Type                | Required | Description                                                                 |
|---------------------------|---------------------|----------|-----------------------------------------------------------------------------|
| `connectTimeout`          | Long                | no       | The maximum time allowed for establishing a connection in milliseconds.     |
| `readTimeout`             | Long                | no       | The maximum time allowed for reading data from the server, in milliseconds. |
| `sendTimeout`             | Long                | no       | The maximum time allowed for writing data to the server, in milliseconds.   |
| `useLoggingInterceptor`   | bool                | no       | Indicates whether a logging interceptor should be included.                 |
| `sslCertificatePinner`    | CertificatePinner   | no       | The addition of an SSL certificate pinner for securing connections.         |
| `sslSocketFactory`        | SSLSocketFactory    | no       | An optional SSLSocketFactory used to configure SSL/TLS connections          |
| `x509TrustManager`        | X509TrustManager    | no       | An optional X509TrustManager used for SSL certificate trust                 |
| `hostnameVerifier`        | HostnameVerifier    | no       | Optional hostname verifier for validating the hostname of the server.       |

#### Get API Client

The createAPI function efficiently sets up an API service. By providing the base URL, network
connection settings, response format, and API interface, a fully functional service for handling
network requests is created.

```kotlin
val apiService = createAPI(
    baseUrl = "https://api.example.com/",
    okHttpClient = myOkHttpClient,
    callAdapterFactory = RxJava3CallAdapterFactory.create(),
    clazz = MyApiService::class.java
)
```

| Parameter Name       | Type                | Required | Description                                                                                                  |
|----------------------|---------------------|----------|--------------------------------------------------------------------------------------------------------------|
| `baseUrl`            | String              | true     | The main URL for the API.                                                                                    |
| `okHttpClient`       | OkHttpClient        | true     | Handles the network connection. Customization options include adding timeouts or logging network calls.      |
| `callAdapterFactory` | CallAdapter.Factory | true     | Determines how API responses are returned, such as in a simple object format or as an RxJava Observable.     |
| `clazz`              | Class<T>            | true     | Defines the API interface, specifying the various endpoints (actions) that can be performed within the code. |

#### Get Chucker Interceptor

Creates a ChuckerInterceptor.Builder configured with the provided Context. This interceptor is used
for inspecting and debugging HTTP requests and responses within the application.

```kotlin
val networkRepository: FeatureNetworkRepository = FeatureNetworkRepositoryImpl()
val chuckerInterceptor = networkRepository.getChuckerInterceptorBuilder(this).build()
val okHttpClient = networkRepository.getOkHttpClientBuilder(useLoggingInterceptor = true)
    .addInterceptor(chuckerInterceptor).build()
```

#### Get Certificate Pinner

Generates a CertificatePinner.Builder, which is used to build a certificate pinner. This pinner
ensures that only specified SSL certificates are accepted for secure connections, enhancing the
security of network communications.

<table>
  <tr>
    <td>
		<img width="250px" src="https://raw.githubusercontent.com/fadlurahmanfdev/kotlin_feature_network/master/media/ssl_peer_unverified_exception.png">
    </td>
  </tr>
</table>

```kotlin
val networkRepository: FeatureNetworkRepository = FeatureNetworkRepositoryImpl()
val jsonPlaceHolderIncorrectSslPinner = networkRepository.getCertificatePinnerBuilder()
    .add(
        "jsonplaceholder.typicode.com",
        "sha256/B17MJoW6Bu9Hl+JStLT4gw+gm3nSDQ3lxuj6xKQrjmU",
        "sha256/e0IRz5Tio3GA1Xs4fUVWmH1xHDiH2dMbVtCBSkOIdqM",
        "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E="
    )
    .build()
val okHttpClient = networkRepository.getOkHttpClientBuilder(
    useLoggingInterceptor = true,
    sslCertificatePinner = jsonPlaceHolderIncorrectSslPinner
).addInterceptor(chuckerInterceptor).build()
```

#### Get Chucker

Chucker is an HTTP Inspector tool for Kotlin which helps debugging http requests. It catches and
stores http requests and responses, which can be viewed via simple UI.

<table>
  <tr>
    <td>
		<img width="250px" src="https://raw.githubusercontent.com/fadlurahmanfdev/kotlin_feature_network/master/media/chucker_notification.png">
    </td>
    <td>
       <img width="250px" src="https://raw.githubusercontent.com/fadlurahmanfdev/kotlin_feature_network/master/media/chucker_page.png">
    </td>
  </tr>
</table>

```kotlin
// Setup Chucker
val chuckerInterceptor = networkRepository.getChuckerInterceptorBuilder(this).build()
val okHttpClient = networkRepository.getOkHttpClientBuilder(
    useLoggingInterceptor = true,
    sslCertificatePinner = jsonPlaceHolderSslPinner
).addInterceptor(chuckerInterceptor).build()
```

#### Logger Interceptor

Logger interceptor is an interceptor for debugging request & response in terminal. It help developer
to debugging HTTP.

<table>
  <tr>
    <td>
		<img width="250px" src="https://raw.githubusercontent.com/fadlurahmanfdev/kotlin_feature_network/master/media/logger_response_part1.png">
    </td>
    <td>
       <img width="250px" src="https://raw.githubusercontent.com/fadlurahmanfdev/kotlin_feature_network/master/media/logger_response_part2.png">
    </td>
  </tr>
</table>

```kotlin
val okHttpClient = networkRepository.getOkHttpClientBuilder(
    useLoggingInterceptor = true, // make logging interceptor -> true
    sslCertificatePinner = jsonPlaceHolderSslPinner
).addInterceptor(chuckerInterceptor).build()
```

