package com.fadlurahmanfdev.example.data.state

sealed class FetchNetworkState() {
    data object IDLE : FetchNetworkState()
    data object LOADING : FetchNetworkState()
    data object SUCCESS : FetchNetworkState()
    class FAILED(val title: String, val message: String) : FetchNetworkState()
}