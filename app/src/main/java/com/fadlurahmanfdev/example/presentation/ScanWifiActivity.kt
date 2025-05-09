package com.fadlurahmanfdev.example.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.presentation.adapter.WifiAdapter
import com.fadlurahmanfdev.networx.NetworxWifi
import com.fadlurahmanfdev.networx.data.model.FeatureWifiInfoModel
import com.fadlurahmanfdev.networx.exception.NetworxException
import com.google.android.material.snackbar.Snackbar

@OptIn(ExperimentalStdlibApi::class)
class ScanWifiActivity : AppCompatActivity(), WifiAdapter.Callback {
    private lateinit var networxWifi: NetworxWifi

    private lateinit var rv: RecyclerView
    private lateinit var main: ConstraintLayout

    private lateinit var adapter: WifiAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan_wifi)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rv = findViewById<RecyclerView>(R.id.rv)
        main = findViewById(R.id.main)

        adapter = WifiAdapter()
        adapter.setCallback(this)
        rv.adapter = adapter


        networxWifi = NetworxWifi(applicationContext)
    }

    override fun onPause() {
        networxWifi.stopScanNearbyWifi(this)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        networxWifi.scanNearbyWifi(this, object : NetworxWifi.ScanWifiCallback {
            override fun onSuccessScanNearbyWifi(wifiResults: List<FeatureWifiInfoModel>) {
                adapter.setList(wifiResults)
            }

            override fun onFailedScanNearbyWifi(exception: NetworxException) {
                val snackbar = Snackbar.make(main, exception.message ?: "-", Snackbar.LENGTH_LONG)
                snackbar.show()
            }

        })
    }

    override fun onClicked(item: FeatureWifiInfoModel) {

    }
}