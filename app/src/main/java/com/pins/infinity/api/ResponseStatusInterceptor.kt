package com.pins.infinity.api

import co.paystack.android.api.model.ApiResponse
import com.google.gson.Gson
import com.pins.infinity.api.exceptions.ApiException
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection.HTTP_OK

class ResponseStatusInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        with(chain.proceed(chain.request())) {
            if (code() != HTTP_OK) {
                body()?.let { body ->
                    val apiResponse = Gson().fromJson(body.string(), ApiResponse::class.java)
                    body.close()
                    throw ApiException(apiResponse.message)
                }
            }
            return this
        }
    }
}