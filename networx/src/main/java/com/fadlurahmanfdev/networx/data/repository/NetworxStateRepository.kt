package com.fadlurahmanfdev.networx.data.repository

import com.fadlurahmanfdev.networx.data.enum.NetworkType

interface NetworxStateRepository {
    fun connectedNetworkType(): NetworkType?
    fun isConnectedToCellular(): Boolean
    fun isConnectedToWifi(): Boolean
    fun isConnectedToEthernet(): Boolean
    fun isConnectedToNetwork(type: NetworkType): Boolean
    fun isConnected(): Boolean
}