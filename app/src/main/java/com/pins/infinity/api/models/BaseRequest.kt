package com.pins.infinity.api.models

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Created by Pavlo Melnyk on 18.01.2019.
 */
open class BaseRequest {
    //TODO: Implement in all after tests
    fun <T> getAsMap(request: BaseRequest?) : Map<String, String> {
        val map = ObjectMapper().convertValue<Map<String, String>>(request?: this as T, object :
                TypeReference<Map<String, String>>() {})
        return map
    }
}