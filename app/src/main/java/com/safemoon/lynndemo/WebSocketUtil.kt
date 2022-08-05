package com.safemoon.lynndemo

import android.os.Handler
import android.os.Looper
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber
import java.net.URI

const val WS_URL =  "wss://monero.akcode.com:12317"

const val TAG = "WebSocket"

class WebSocketUtil(var webInterface: MyWebInterface? = null) {

    private val mWebSocketClient = object : WebSocketClient(URI(WS_URL), Draft_6455(), null, 30000) {
        override fun onOpen(handshakedata: ServerHandshake?) {
            webInterface?.onOpen(handshakedata)
            Timber.tag("Lynn").d("20 WSU onOpen oooooooooooooooooooooooooooooopen" );
        }

        override fun onMessage(message: String?) {
            webInterface?.onMessage(message)
            Timber.tag("Lynn").d("33 WSU onMessage=" + message);
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            webInterface?.onClose(code, reason, remote)
            Timber.tag("Lynn").d("49 WSU closing socket xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx $reason" );
            //autoReconnect()
        }

        override fun onError(ex: Exception?) {
            webInterface?.onError(ex)
            Timber.tag("Lynn").d("56 WSU onError socket xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx ${ex?.localizedMessage}" );
        }
    }

    init {
        mWebSocketClient.connect()
    }

    fun get() = mWebSocketClient

    fun closeSocket(){
        mWebSocketClient.close()
    }

    fun openSocket() {
        mWebSocketClient.reconnect()
    }

    fun autoReconnect() {
        Handler(Looper.getMainLooper()).postDelayed({ mWebSocketClient.reconnect() }, 5000)
    }
}


open class MyWebInterface : IWebInterface {
    override fun onOpen(handshakedata: ServerHandshake?) {

    }

    override fun onMessage(message: String?) {

    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {

    }

    override fun onError(ex: Exception?) {

    }
}

interface IWebInterface {
    fun onOpen(handShakeData: ServerHandshake?)
    fun onMessage(message: String?)
    fun onClose(code: Int, reason: String?, remote: Boolean)
    fun onError(ex: Exception?)
}