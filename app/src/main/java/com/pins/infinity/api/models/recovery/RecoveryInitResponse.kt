package com.pins.infinity.api.models.recovery

import com.google.gson.annotations.SerializedName
import com.pins.infinity.api.exceptions.ApiParseException
import com.pins.infinity.model.RecoveryInit

data class RecoveryInitResponse(
        @SerializedName("response") val response: RecoveryInitResponseData?,
        val message: String,
        val code: Long,
        val error: Boolean
)

data class RecoveryInitResponseData(
        @SerializedName("token") val token: String?,
        @SerializedName("recovery_mode") val mode: String?
)

fun RecoveryInitResponse.toDomain(): RecoveryInit {
    val data = this as? RecoveryInitResponse
            ?: throw ApiParseException("RecoveryInitResponse cast exception")
    return RecoveryInit(
            token = data.response?.token ?: throw ApiParseException("token = null"),
            mode = data.response.mode ?: throw ApiParseException("mode = null")
    )
}