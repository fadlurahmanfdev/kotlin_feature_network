package com.fadlurahmanfdev.example.domain.interceptor

import android.util.Log
import com.fadlurahmanfdev.networx.data.enum.SHA
import com.fadlurahmanfdev.networx.utils.NetworxUtils
import okhttp3.Interceptor
import okhttp3.Response
import javax.net.ssl.SSLHandshakeException

class ExampleHTTPFingerprintInterceptor(val correct: Boolean) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        Log.d(
            this::class.java.simpleName,
            "Example-Networx-LOG %%% requested URL: ${url.toUrl().toString()}"
        )
        val headers: HashMap<String, String> = hashMapOf()
        request.headers.forEach { pair ->
            headers[pair.first] = pair.second
        }
        Log.d(this::class.java.simpleName, "Example-Networx-LOG %%% requested header: ${headers}")

        // Get From Remote Config / Storage / Other
        val allowedFingerprint: List<String>
        if (correct) {
            allowedFingerprint = listOf<String>(
                "0a90b779d798ac916c9b9f04340bf2e9671be24777842b8502350763045fac8e"
            )
        } else {
            allowedFingerprint = listOf<String>(
                "3461aaf30b87e6ffb74969ae7a96efa4e8e1c6a2a39a213ed3abd5d17de9279f"
            )
        }

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