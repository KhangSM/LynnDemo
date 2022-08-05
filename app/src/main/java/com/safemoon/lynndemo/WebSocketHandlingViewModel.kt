package com.safemoon.lynndemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber


class WebSocketHandlingViewModel(application: Application) : AndroidViewModel(application) {
    private var callBack : ((ServerHandshake?)->Unit)? = null
    private val myWebInterface = object : MyWebInterface() {

        override fun onMessage(message: String?) {
            viewModelScope.launch {
                getFromSocket(message)
            }
        }

        override fun onOpen(handshakedata: ServerHandshake?) {
            super.onOpen(handshakedata)
            callBack?.invoke(handshakedata)
        }

        override fun onError(ex: Exception?) {
            super.onError(ex)
            viewModelScope.launch {
                getFromSocket("CONNECTION_REFUSED")
            }
        }

    }

    private val webSocket = WebSocketUtil(myWebInterface)
    private val webClient = webSocket.get()

    fun sendMessage(request: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                webClient.let {
                    Timber.tag("Lynn").d("50 WSHVM isOpen=%s", it.isOpen)
                    if (it.isOpen) {
                        webClient.send(request)
                    } else {
                        openSocket()
                        Timber.tag("Lynn").d("56 WSHVM isOpen=%s", it.isOpen)
                        callBack = {
                            callBack = null
                            webClient.send(request)
                        }
                    }
                }
            }
        }
    }

    val response = MutableLiveData<String?>()

    //Call Will Listen At activity
    private fun getFromSocket(message: String?) {
        response.postValue(message)
    }

    fun simulate() {}

    fun closeSocket() {
        Timber.tag("Lynn").d("77 WSHVM close socket (viewmodels)")
        webSocket.closeSocket()
    }

    fun openSocket() {
        Timber.tag("Lynn").d("82 WSHVM open socket (viewmodels)")
        webSocket.openSocket()
    }
}