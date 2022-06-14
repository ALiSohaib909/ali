package com.pins.infinity.api.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Pavlo Melnyk on 2018-12-05.
 */

data class InitTransactionResponse(
        @SerializedName("message") val message: String,
        @SerializedName("code") val code: Long,
        @SerializedName("response") val response: Response,
        @SerializedName("error") val error: Boolean) {

    data class Response(
            @SerializedName("total_in_unit") val totalInUnit: Long,
            @SerializedName("recovery") val recovery: String,
            @SerializedName("method") val method: String,
            @SerializedName("currency") val currency: String,
            @SerializedName("amount") val amount: Long,
            @SerializedName("trans_id") val transactionId: String,
            @SerializedName("total") val total: Double,
            @SerializedName("gateway") val gateway: String,
            @SerializedName("vat") val vat: Long,
            @SerializedName("description") val description: String
    )
}

