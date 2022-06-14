package com.pins.infinity.api.models

/**
 * Created by Pavlo Melnyk on 2018-12-09.
 */
data class CompleteTransactionResponse (
        val message: String,
        val code: Long,
        val error: Boolean
)

