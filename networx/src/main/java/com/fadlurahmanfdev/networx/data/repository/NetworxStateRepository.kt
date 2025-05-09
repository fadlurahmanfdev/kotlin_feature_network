package com.fadlurahmanfdev.networx.data.repository

import android.app.Activity
import com.fadlurahmanfdev.networx.data.enum.NetworkType

interface NetworxStateRepository {
    /**
     * Check current connected network type.
     *
     * @return [NetworkType] connected network type. (e.g., [NetworkType.WIFI], [NetworkType.CELLULAR], [NetworkType.ETHERNET], etc)
     * */
    fun connectedNetworkType(): NetworkType?
    /**
     * Listen network state whether the configurable network of device is changed.
     * */
    fun listenNetworkState(activity: Activity, listener: NetworxStateListener)
    /**
     * Check if device is connected to the internet through cellular
     * */
    fun isConnectedToCellular(): Boolean
    /**
     * Check if device is connected to the internet through wifi
     * */
    fun isConnectedToWifi(): Boolean
    /**
     * Check if device is connected to the internet through ethernet
     * */
    fun isConnectedToEthernet(): Boolean
    /**
     * Check if device is connected to the internet through vpn
     * */
    fun isConnectedToVPN(): Boolean
    /**
     * Check if device is connected to specific network type like wifi, cellular, etc.
     *
     * @param type network type (e.g., wifi, cellular)
     * */
    fun isConnectedToNetwork(type: NetworkType): Boolean
    /**
     * Check whether device is connected through network.
     * */
    fun isConnected(): Boolean
}