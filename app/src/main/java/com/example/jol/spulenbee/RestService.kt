package com.example.jol.spulenbee

import com.loopj.android.http.*

object RestService {
    private val BASE_URL = "http://192.168.0.100:5000/getstate"

    private val client = AsyncHttpClient()

    operator fun get(url: String, params: RequestParams?, responseHandler: AsyncHttpResponseHandler) {
        client.setBasicAuth("username", "password")
        client.get(getAbsoluteUrl(url), params, responseHandler)
    }

    fun post(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler)
    }

    private fun getAbsoluteUrl(relativeUrl: String): String {
        return BASE_URL + relativeUrl
    }
}
