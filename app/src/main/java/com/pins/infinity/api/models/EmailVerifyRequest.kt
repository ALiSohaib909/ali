package com.pins.infinity.api.models

import retrofit2.http.Query

/**
 * Created by Pavlo Melnyk on 16.01.2019.
 */

data class EmailVerifyRequest(
        @Query("first_name") val firstName: String?,
        @Query("last_name") val lastName: String?,
        @Query("phone") val phone: String?,
        @Query("email") val email: String?
) : BaseRequest()