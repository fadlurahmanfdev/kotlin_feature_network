package com.fadlurahmanfdev.networx.constant

import com.fadlurahmanfdev.networx.exception.NetworxException

object NetworxExceptionConstant {
    val UNKNOWN_NETWORK_TYPE = NetworxException(
        code = "UNKNOWN_NETWORK_TYPE",
        message = "Unknown network type detected"
    )

    val UNSUPPORTED_SDK_SCAN_WIFI = NetworxException(
        code = "UNSUPPORTED_SDK_SCAN_WIFI",
        message = "Unsupported sdk for scan nearby wifi"
    )
    val UNABLE_SCAN_NEARBY_WIFI = NetworxException(
        code = "UNABLE_SCAN_NEARBY_WIFI",
        message = "Something happen, unable to scan nearby wifi"
    )
    val UNABLE_SCAN_NEARBY_WIFI_CAUSED_BY_ACCESS_FINE_LOCATION = NetworxException(
        code = "ACCESS_FINE_LOCATION_PERMISSION_NOT_ENABLED",
        message = "Unable to access precise/fine location, access precise/fine location permission must be enabled"
    )
    val UNABLE_SCAN_NEARBY_WIFI_CAUSED_BY_CHANGE_WIFI_STATE_PERMISSION = NetworxException(
        code = "CHANGE_WIFI_STATE_PERMISSION_NOT_ENABLED",
        message = "Unable to scan wifi, wifi state permission must be enabled"
    )
    val UNABLE_SCAN_NEARBY_WIFI_CAUSED_BY_GPS_LOCATION_NOT_ENABLED = NetworxException(
        code = "GPS_LOCATION_NOT_ENABLED",
        message = "Unable to scan wifi, location service must be enabled"
    )

    // P2P EXCEPTION
    val P2P_CHANNEL_NOT_INITIALIZED = NetworxException(
        code = "P2P_CHANNEL_NOT_INITIALIZED",
        message = "P2P Channel not initialized yet"
    )
}