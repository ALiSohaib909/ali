package com.pins.infinity.api.usermodels

/**
 * Created by Pavlo Melnyk on 13.02.2019.
 */

data class UserRequest(
        val userId: String,
        val firstName: String? = null,
        val lastName: String? = null,
        val phone: String? = null,
        val token: String? = null,
        var verified: Boolean? = null,
        var email: String? = null,
        var activeIntruder: Boolean? = null,
        var plan: String? = null)