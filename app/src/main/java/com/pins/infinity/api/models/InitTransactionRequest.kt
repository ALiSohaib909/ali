package com.pins.infinity.api.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Pavlo Melnyk on 2018-12-05.
 */
class InitTransactionRequest(
        @SerializedName("plan") val plan: String,
        @SerializedName("duration") val duration: String,
        @SerializedName("gateway") val gateway: String,
        @SerializedName("method") val method: String,
        @SerializedName("app") val app: String,
        @SerializedName("recovery") val recovery: String
) : BaseRequest()