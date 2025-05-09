package com.fadlurahmanfdev.networx

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import com.fadlurahmanfdev.networx.constant.NetworxExceptionConstant
import com.fadlurahmanfdev.networx.data.model.FeatureWifiInfoModel
import com.fadlurahmanfdev.networx.exception.NetworxException

@ExperimentalStdlibApi
class NetworxWifi(context: Context) {
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val locationService =
        context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val networxManager = NetworxManager(context)
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var scanWifiCallback: ScanWifiCallback? = null
    private val wifiResult = arrayListOf<FeatureWifiInfoModel>()
    private var _isProcessScanningWifiNearby = false

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val isSuccess = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (isSuccess) {
                    val isConnectedThroughWifi = networxManager.isConnectedToWifi()
                    var connectedSsid: String? = null
                    var connectedBssid: String? = null
                    if (isConnectedThroughWifi) {
                        val capabilities =
                            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            // Somehow result from wifi info not enough, need research further more
                            val wifiInfo = capabilities?.transportInfo as WifiInfo
                            connectedSsid = wifiInfo.ssid
                            connectedBssid = wifiInfo.bssid

                            val connectedWifiInfo = wifiManager.connectionInfo
                            connectedSsid = connectedWifiInfo.ssid.removeSurrounding("\"")
                            connectedBssid = connectedWifiInfo.bssid
                        } else {
                            val connectedWifiInfo = wifiManager.connectionInfo
                            connectedSsid = connectedWifiInfo.ssid.removeSurrounding("\"")
                            connectedBssid = connectedWifiInfo.bssid
                        }
                    }

                    wifiResult.clear()
                    wifiResult.addAll(wifiManager.scanResults.map { scanResult ->
                        var wifiName: String? = null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            wifiName = scanResult.wifiSsid?.toString()
                        } else {
                            wifiName = scanResult.SSID
                        }
                        FeatureWifiInfoModel(
                            name = wifiName,
                            address = scanResult.BSSID,
                            isConnected = scanResult.SSID == connectedSsid && scanResult.BSSID == connectedBssid
                        )
                    }.toList().sortedByDescending { model ->
                        model.isConnected
                    })
                    scanWifiCallback?.onSuccessScanNearbyWifi(wifiResult)
                } else {
                    scanWifiCallback?.onFailedScanNearbyWifi(
                        NetworxException(
                            code = NetworxExceptionConstant.UNABLE_SCAN_NEARBY_WIFI.code,
                            message = NetworxExceptionConstant.UNABLE_SCAN_NEARBY_WIFI.message,
                        )
                    )
                }
            } else {
                scanWifiCallback?.onFailedScanNearbyWifi(
                    NetworxException(
                        code = NetworxExceptionConstant.UNSUPPORTED_SDK_SCAN_WIFI.code,
                        message = NetworxExceptionConstant.UNSUPPORTED_SDK_SCAN_WIFI.message,
                    )
                )
            }
        }
    }

    fun scanNearbyWifi(activity: Activity, callback: ScanWifiCallback) {
        if (_isProcessScanningWifiNearby) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!locationService.isLocationEnabled) {
                throw NetworxException(
                    code = NetworxExceptionConstant.UNABLE_SCAN_NEARBY_WIFI_CAUSED_BY_GPS_LOCATION_NOT_ENABLED.code,
                    message = NetworxExceptionConstant.UNABLE_SCAN_NEARBY_WIFI_CAUSED_BY_GPS_LOCATION_NOT_ENABLED.message,
                )
            }
        }

        scanWifiCallback = callback
        val intentFilter = IntentFilter().apply {
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        }
        try {
            activity.registerReceiver(wifiScanReceiver, intentFilter)
            val success = wifiManager.startScan()
            if (success) {
                _isProcessScanningWifiNearby = true
                Log.d(this::class.java.simpleName, "successfully scan nearby wifi")
            } else {
                Log.w(this::class.java.simpleName, "failed to scan nearby wifi")
            }
        } catch (e: Throwable) {
            Log.e(this::class.java.simpleName, "failed to scan nearby wifi: ${e.message}")
        }
    }

    fun stopScanNearbyWifi(activity: Activity) {
        if (_isProcessScanningWifiNearby) {
            activity.unregisterReceiver(wifiScanReceiver)
            scanWifiCallback = null
            _isProcessScanningWifiNearby = false
        }
    }

    interface ScanWifiCallback {
        fun onSuccessScanNearbyWifi(wifiResults: List<FeatureWifiInfoModel>)
        fun onFailedScanNearbyWifi(exception: NetworxException)
    }
}