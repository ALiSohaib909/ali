package com.pins.infinity.api.models.recovery

import com.google.gson.annotations.SerializedName
import com.pins.infinity.api.exceptions.ApiParseException
import com.pins.infinity.model.RecoveryCheck
import com.pins.infinity.model.RecoveryInit

data class RecoveryCheckResponse (
        @SerializedName("message")val message: String,
        @SerializedName("code")val code: Long,
        @SerializedName("response")val response: RecoveryCheckResponseData?,
        @SerializedName("error")val error: Boolean
)

data class RecoveryCheckResponseData(
        @SerializedName("token") val token: String?
)

fun RecoveryCheckResponse.toDomain() = RecoveryCheck(
        token = response?.token ?: throw ApiParseException("token = null")
)