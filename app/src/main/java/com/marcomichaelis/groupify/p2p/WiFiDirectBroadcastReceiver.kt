package com.marcomichaelis.groupify.p2p

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.widget.Toast
import java.net.InetAddress
import java.util.concurrent.Executors

class WiFiDirectBroadcastReceiver
constructor(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val onDeviceListChange: (WifiP2pDeviceList) -> Unit,
    private val onReceiveGroupOwnerAddress: (InetAddress) -> Unit
) : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                Toast.makeText(
                        context,
                        "WiFi direct is ${if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) "available" else "not available"}!",
                        Toast.LENGTH_SHORT
                    )
                    .show()
                manager.discoverPeers(
                    channel,
                    object : WifiP2pManager.ActionListener {
                        override fun onSuccess() = println("Discovering peers.")
                        override fun onFailure(resultCode: Int) =
                            println("Failed to start discovery: $resultCode")
                    }
                )
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val networkInfo =
                    intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                println(networkInfo)
                if (networkInfo?.isConnected == true) {
                    manager.requestConnectionInfo(channel) { info ->
                        println(info)
                        onReceiveGroupOwnerAddress(info.groupOwnerAddress)
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(channel) { onDeviceListChange(it) }
            }
        }
    }
}
