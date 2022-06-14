
package com.pins.infinity.api.models

import androidx.room.PrimaryKey
import androidx.annotation.NonNull
import com.google.gson.annotations.SerializedName

/**
 * Created by Pavlo Melnyk on 16.01.2019.
 */

data class EmailVerifyResponse (
        @PrimaryKey(autoGenerate = true) @NonNull val id: Int,

        @SerializedName("message") val message: String,
        @SerializedName("code") val code: Long,
        @SerializedName("response") val response: Response,
        @SerializedName("error") val error: Boolean
)