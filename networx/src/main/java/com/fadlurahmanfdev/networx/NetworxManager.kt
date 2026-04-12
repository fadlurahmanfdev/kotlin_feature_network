package com.fadlurahmanfdev.networx

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import com.fadlurahmanfdev.networx.constant.NetworxExceptionConstant
import com.fadlurahmanfdev.networx.data.enum.NetworkType
import com.fadlurahmanfdev.networx.data.repository.NetworxStateListener
import com.fadlurahmanfdev.networx.data.repository.NetworxStateRepository

class NetworxManager(context: Context) : NetworxStateRepository {
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var _connectedNetworkType: NetworkType? = null

    /**
     * Check current connected network type.
     *
     * @return [NetworkType] connected network type. (e.g., [NetworkType.WIFI], [NetworkType.CELLULAR], [NetworkType.ETHERNET], etc)
     * */
    override fun connectedNetworkType(): NetworkType? = _connectedNetworkType

    private var networxStateListener: NetworxStateListener? = null
    private var _isConnectedToInternet: Boolean = false

    init {
        _isConnectedToInternet = isConnected()

        if (_isConnectedToInternet) {
            if (isConnectedToEthernet()) {
                _connectedNetworkType = NetworkType.ETHERNET
            }

            if (isConnectedToVPN()) {
                _connectedNetworkType = NetworkType.VPN
            }

            if (isConnectedToWifi()) {
                _connectedNetworkType = NetworkType.WIFI
            }

            if (isConnectedToCellular()) {
                _connectedNetworkType = NetworkType.CELLULAR
            }
        }
    }

    private var networxTypeChangedListener = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val hasTransportEthernet =
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            if (hasTransportEthernet && connectedNetworkType() != NetworkType.ETHERNET) {
                _connectedNetworkType = NetworkType.ETHERNET
                networxStateListener?.onConnectedNetworkTypeChange(NetworkType.ETHERNET)
                return
            }

            val hasTransportVPN =
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
            if (hasTransportVPN && connectedNetworkType() != NetworkType.VPN) {
                _connectedNetworkType = NetworkType.VPN
                networxStateListener?.onConnectedNetworkTypeChange(NetworkType.VPN)
                return
            }

            val hasTransportWifi =
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            if (hasTransportWifi && connectedNetworkType() != NetworkType.WIFI) {
                _connectedNetworkType = NetworkType.WIFI
                networxStateListener?.onConnectedNetworkTypeChange(NetworkType.WIFI)
                return
            }

            val hasTransportCellular =
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            if (hasTransportCellular && connectedNetworkType() != NetworkType.CELLULAR) {
                _connectedNetworkType = NetworkType.CELLULAR
                networxStateListener?.onConnectedNetworkTypeChange(NetworkType.CELLULAR)
                return
            }
        }
    }

    private var isNetworkConnectedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val networkInfo = connectivityManager.activeNetworkInfo

                val isConnected = networkInfo?.isConnectedOrConnecting ?: false
                if (isConnected != _isConnectedToInternet) {
                    var networkType: NetworkType? = null
                    if (isConnected) {
                        networkType = when (networkInfo?.type) {
                            ConnectivityManager.TYPE_ETHERNET -> {
                                NetworkType.ETHERNET
                            }

                            ConnectivityManager.TYPE_WIFI -> {
                                NetworkType.WIFI
                            }

                            ConnectivityManager.TYPE_MOBILE -> {
                                NetworkType.CELLULAR
                            }

                            else -> {
                                NetworkType.UNKNOWN
                            }
                        }
                    }
                    _isConnectedToInternet = isConnected
                    networxStateListener?.onConnectedChange(isConnected, networkType)
                    return
                }
            }
        }
    }

    /**
     * Listen network state whether the configurable network of device is changed.
     * */
    override fun listenNetworkState(activity: Activity, listener: NetworxStateListener) {
        Log.i(this::class.java.simpleName, "Networx-LOG %%% starting to listen network state")
        _isConnectedToInternet = isConnected()

        activity.registerReceiver(
            isNetworkConnectedReceiver,
            IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        )

        networxStateListener = listener

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networxTypeChangedListener)
        Log.i(
            this::class.java.simpleName,
            "Networx-LOG %%% currently successfully to listen network state"
        )
    }

    /**
     * Remove Listener network state to detect configurable network state changed
     * */
    fun removeListenerNetworkState(activity: Activity) {
        Log.i(this::class.java.simpleName, "Networx-LOG %%% removing network state listener")
        connectivityManager.unregisterNetworkCallback(networxTypeChangedListener)
        networxStateListener = null

        activity.unregisterReceiver(isNetworkConnectedReceiver)
        Log.i(
            this::class.java.simpleName,
            "Networx-LOG %%% successfully removed network state listener"
        )
    }

    /**
     * Check if device is connected to the internet through wifi
     * */
    override fun isConnectedToWifi(): Boolean = isConnectedToNetwork(NetworkType.WIFI)

    /**
     * Check if device is connected to the internet through cellular
     * */
    override fun isConnectedToCellular(): Boolean = isConnectedToNetwork(NetworkType.CELLULAR)

    /**
     * Check if device is connected to the internet through vpn
     * */
    override fun isConnectedToVPN(): Boolean = isConnectedToNetwork(NetworkType.VPN)

    /**
     * Check if device is connected to the internet through ethernet
     * */
    override fun isConnectedToEthernet(): Boolean = isConnectedToNetwork(NetworkType.ETHERNET)

    /**
     * Check if device is connected to specific network type like wifi, cellular, etc.
     *
     * @param type network type (e.g., wifi, cellular)
     * */
    override fun isConnectedToNetwork(type: NetworkType): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val network = connectivityManager.activeNetwork
                val networkCapabilities = when (type) {
                    NetworkType.WIFI -> NetworkCapabilities.TRANSPORT_WIFI
                    NetworkType.CELLULAR -> NetworkCapabilities.TRANSPORT_CELLULAR
                    NetworkType.ETHERNET -> NetworkCapabilities.TRANSPORT_ETHERNET
                    NetworkType.VPN -> NetworkCapabilities.TRANSPORT_VPN
                    else -> throw NetworxExceptionConstant.UNKNOWN_NETWORK_TYPE
                }
                connectivityManager.getNetworkCapabilities(network)
                    ?.hasTransport(networkCapabilities) ?: false
            }

            else -> {
                val connectivityType = when (type) {
                    NetworkType.WIFI -> ConnectivityManager.TYPE_WIFI
                    NetworkType.CELLULAR -> ConnectivityManager.TYPE_MOBILE
                    NetworkType.ETHERNET -> ConnectivityManager.TYPE_ETHERNET
                    NetworkType.VPN -> ConnectivityManager.TYPE_VPN
                    else -> throw NetworxExceptionConstant.UNKNOWN_NETWORK_TYPE
                }
                connectivityManager.activeNetworkInfo?.type == connectivityType
            }
        }
    }

    /**
     * Check whether device is connected through network.
     * */
    override fun isConnected(): Boolean =
        isConnectedToWifi() || isConnectedToCellular() || isConnectedToEthernet() || isConnectedToVPN()
}