package com.pins.infinity.api.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Pavlo Melnyk on 2018-12-09.
 */
data class CompleteTransactionRequest(
        @SerializedName("status") val status: String,
        @SerializedName("affiliate_id") val affiliateId: String,
        @SerializedName("trans_ref") val transactionReference: String)