package com.fadlurahmanfdev.networx.data.enum

import android.net.NetworkCapabilities

enum class NetworkType(val rawCode: Int) {
    WIFI(NetworkCapabilities.TRANSPORT_WIFI),
    CELLULAR(NetworkCapabilities.TRANSPORT_CELLULAR),
    ETHERNET(NetworkCapabilities.TRANSPORT_ETHERNET),
    UNKNOWN(-1),
}