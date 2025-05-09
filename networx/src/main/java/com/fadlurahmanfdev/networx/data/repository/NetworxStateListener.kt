package com.fadlurahmanfdev.networx.data.repository

import com.fadlurahmanfdev.networx.data.enum.NetworkType

interface NetworxStateListener {
    /**
     * Listen whether the configurable network type in the device changed
     * */
    fun onConnectedNetworkTypeChange(type: NetworkType)

    /**
     * Listen whether the internet connection of device is changed
     * */
    fun onConnectedChange(connected: Boolean, networkType: NetworkType?)
}