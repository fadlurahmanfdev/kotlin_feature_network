package com.fadlurahmanfdev.networx

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import android.util.Log
import com.fadlurahmanfdev.networx.constant.NetworxExceptionConstant
import com.fadlurahmanfdev.networx.exception.NetworxException

@ExperimentalStdlibApi
class NetworxWifiP2P(context: Context) {
    private val wifip2pManager =
        context.applicationContext.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private var p2pChannel: WifiP2pManager.Channel? = null
    private var scanNearbyP2PCallback: ScanNearbyP2PCallback? = null

    fun initializeP2P(activity: Activity) {
        p2pChannel = wifip2pManager.initialize(activity, Looper.getMainLooper(), null)
        Log.i(this::class.java.simpleName, "successfully initialize p2p connection")
    }

    fun disconnectP2P() {
        if (p2pChannel == null) {
            Log.i(
                this::class.java.simpleName,
                "p2p channel missing, either already disconnected or not initialized yet"
            )
            return
        }
        wifip2pManager.cancelConnect(p2pChannel!!, null)
        p2pChannel = null
    }

    private val scanP2pDevicesReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    Log.d(
                        this@NetworxWifiP2P::class.java.simpleName,
                        "on action: ${WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION}"
                    )
                    wifip2pManager.requestPeers(
                        p2pChannel!!,
                        object : WifiP2pManager.PeerListListener {
                            override fun onPeersAvailable(peers: WifiP2pDeviceList?) {
                                Log.d(
                                    this@NetworxWifiP2P::class.java.simpleName,
                                    "total p2p device found: ${peers?.deviceList?.size}"
                                )
                                scanNearbyP2PCallback?.onP2PDeviceFound(
                                    peers?.deviceList?.toList() ?: listOf()
                                )
                            }
                        })
                }
            }
        }
    }

    fun discoverPeers(activity: Activity, callback: ScanNearbyP2PCallback) {
        if (p2pChannel == null) {
            throw NetworxException(
                code = NetworxExceptionConstant.P2P_CHANNEL_NOT_INITIALIZED.code,
                message = NetworxExceptionConstant.P2P_CHANNEL_NOT_INITIALIZED.message
            )
        }

        scanNearbyP2PCallback = callback

        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        }
        activity.registerReceiver(scanP2pDevicesReceiver, intentFilter)
        wifip2pManager.discoverPeers(p2pChannel!!, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.i(this@NetworxWifiP2P::class.java.simpleName, "successfully discover peer")
            }

            override fun onFailure(reason: Int) {
                Log.e(
                    this@NetworxWifiP2P::class.java.simpleName,
                    "failed discover peer: $reason"
                )
            }
        })
    }

    fun stopDiscoveryP2P(activity: Activity) {
        wifip2pManager.stopPeerDiscovery(p2pChannel!!, null)
        activity.unregisterReceiver(scanP2pDevicesReceiver)
        scanNearbyP2PCallback = null
    }

    fun connectP2P(device: WifiP2pDevice, callback: WifiP2PConnectionCallback) {
        val config = WifiP2pConfig().apply {
            this.deviceAddress = device.deviceAddress
        }
        wifip2pManager.connect(p2pChannel!!, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                callback.onSuccessfullyConnectP2P()
            }

            override fun onFailure(reason: Int) {
                callback.onFailedConnectP2P()
            }
        })
    }

    interface ScanNearbyP2PCallback {
        fun onP2PDeviceFound(devices: List<WifiP2pDevice>)
    }

    interface WifiP2PConnectionCallback {
        fun onSuccessfullyConnectP2P()
        fun onFailedConnectP2P()
    }
}