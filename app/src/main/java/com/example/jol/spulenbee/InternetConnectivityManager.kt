@file:Suppress("DEPRECATION")

package com.example.jol.spulenbee

import android.annotation.SuppressLint
import android.content.Context

object InternetConnectivityManager {
    fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as android.net.ConnectivityManager

        @SuppressLint("MissingPermission")
        val wifiNetwork = cm.getNetworkInfo(android.net.ConnectivityManager.TYPE_WIFI)
        return wifiNetwork != null && wifiNetwork.isConnected
    }
}