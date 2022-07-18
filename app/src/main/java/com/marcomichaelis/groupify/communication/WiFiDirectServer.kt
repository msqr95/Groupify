package com.marcomichaelis.groupify.communication

interface WiFiDirectServer {
    fun start()
    fun stop()

    fun publish(topic: String, message: String)
}
