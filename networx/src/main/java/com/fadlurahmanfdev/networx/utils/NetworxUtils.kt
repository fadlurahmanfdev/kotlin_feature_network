package com.fadlurahmanfdev.networx.utils

import com.fadlurahmanfdev.networx.data.enum.SHA
import java.net.URL
import java.security.MessageDigest
import java.security.cert.Certificate
import javax.net.ssl.HttpsURLConnection

object NetworxUtils {
    /**
     * Check whether connection is using correct fingerprint
     *
     * @param timeout timeout in seconds
     * */
    fun isUsingCorrectFingerprint(
        serverURL: String,
        allowedFingerprints: List<String>,
        httpHeaderArgs: Map<String, String>,
        timeout: Int,
        type: SHA
    ): Boolean {
        val sha: String = this.getFingerprint(serverURL, timeout, httpHeaderArgs, type)
        return allowedFingerprints.map { fp -> fp.uppercase().replace("\\s".toRegex(), "") }
            .contains(sha)
    }

    private fun getFingerprint(
        httpsURL: String,
        connectTimeout: Int,
        httpHeaderArgs: Map<String, String>,
        type: SHA
    ): String {
        val url = URL(httpsURL)
        val httpClient: HttpsURLConnection = url.openConnection() as HttpsURLConnection
        if (connectTimeout > 0)
            httpClient.connectTimeout = connectTimeout * 1000
        httpHeaderArgs.forEach { (key, value) -> httpClient.setRequestProperty(key, value) }

        httpClient.connect()

        val cert: Certificate = httpClient.serverCertificates[0] as Certificate
        return hashString(type, cert.encoded)
    }

    private fun hashString(type: SHA, input: ByteArray) =
        MessageDigest
            .getInstance(type.value)
            .digest(input)
            .map { String.format("%02X", it) }
            .joinToString(separator = "")
}