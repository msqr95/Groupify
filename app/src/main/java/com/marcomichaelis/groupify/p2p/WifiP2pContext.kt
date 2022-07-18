package com.marcomichaelis.groupify.p2p

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import androidx.compose.runtime.compositionLocalOf
import java.net.InetAddress
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

val LocalWifiP2pContext = compositionLocalOf { WifiP2pContext() }

enum class Type {
    HOST,
    PEER,
    UNKNOWN
}

open class WifiP2pContext {
    private val intentFilter =
        IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }

    var type = Type.UNKNOWN
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var receiver: BroadcastReceiver
    val groupOwners =
        MutableSharedFlow<List<WifiDevice>>(
            replay = 1,
            extraBufferCapacity = 0,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    var groupOwnerAddress =
        MutableSharedFlow<InetAddress>(
            replay = 1,
            extraBufferCapacity = 0,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    @SuppressLint("MissingPermission")
    fun initialize(context: Context, manager: WifiP2pManager) {
        this.manager = manager
        channel =
            manager.initialize(context, Looper.getMainLooper(), null).also { channel ->
                receiver =
                    WiFiDirectBroadcastReceiver(
                        manager,
                        channel,
                        onDeviceListChange = {
                            println("devices ${it.deviceList}")
                            runBlocking {
                                groupOwners.emit(
                                    (if (it.deviceList.any { it.isGroupOwner })
                                            it.deviceList.sortedBy { it.isGroupOwner }.toList()
                                        else it.deviceList.toList())
                                        .map { device ->
                                            WifiDevice(device.deviceAddress, device.deviceName)
                                        }
                                )
                            }
                        },
                        onReceiveGroupOwnerAddress = {
                            println("receive address $it")
                            groupOwnerAddress.tryEmit(it)
                        }
                    )
            }
    }

    @SuppressLint("MissingPermission")
    fun createGroup() {
        type = Type.HOST
        tryRemoveOldGroup {
            manager.createGroup(
                channel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() = println("Created group")
                    override fun onFailure(p0: Int) = println("Failed to create group")
                }
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(device: WifiDevice, callback: (success: Boolean) -> Unit) {
        println("connecting to ${device.deviceAddress}")
        val config =
            WifiP2pConfig().apply {
                deviceAddress = device.deviceAddress
                wps.setup = WpsInfo.PBC
                groupOwnerIntent = 15 // remote device should be the owner
            }
        type = Type.PEER
        manager.connect(
            channel,
            config,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() = callback(true)
                override fun onFailure(resultCode: Int) = callback(false)
            }
        )
    }

    private fun tryRemoveOldGroup(onComplete: () -> Unit) {
        manager.removeGroup(
            channel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    println("Removed old group")
                    onComplete()
                }
                override fun onFailure(p0: Int) = onComplete()
            }
        )
    }

    fun destroy() {
        channel.close()
    }

    fun registerReceiver(context: Context) {
        context.registerReceiver(receiver, intentFilter)
    }

    fun unregisterReceiver(context: Context) {
        context.unregisterReceiver(receiver)
    }
}
