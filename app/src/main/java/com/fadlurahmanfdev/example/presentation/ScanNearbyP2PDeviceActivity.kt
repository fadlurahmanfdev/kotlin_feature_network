package com.fadlurahmanfdev.example.presentation

import android.net.wifi.p2p.WifiP2pDevice
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.presentation.adapter.P2PDeviceAdapter
import com.fadlurahmanfdev.networx.NetworxWifiP2P

@OptIn(ExperimentalStdlibApi::class)
class ScanNearbyP2PDeviceActivity : AppCompatActivity(), P2PDeviceAdapter.Callback {
    private lateinit var featureWifi: NetworxWifiP2P

    private lateinit var rv: RecyclerView

    private lateinit var adapter: P2PDeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan_nearby_p2_pdevice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rv = findViewById<RecyclerView>(R.id.rv)

        adapter = P2PDeviceAdapter()
        adapter.setCallback(this)
        rv.adapter = adapter

        featureWifi = NetworxWifiP2P(applicationContext)
        featureWifi.initializeP2P(this)
        featureWifi.discoverPeers(this, object : NetworxWifiP2P.ScanNearbyP2PCallback {
            override fun onP2PDeviceFound(devices: List<WifiP2pDevice>) {
                adapter.setList(devices)
            }
        })
    }

    override fun onClicked(item: WifiP2pDevice) {

    }
}