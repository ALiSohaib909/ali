package com.pins.infinity.api.utils

import java.lang.reflect.Type
import com.google.gson.*


/**
 * Created by Pavlo Melnyk on 01.02.2019.
 */

class BooleanTypeAdapterDeserialize : JsonDeserializer<Boolean> {
    override fun deserialize(json: JsonElement, typeOfT: Type,
                             context: JsonDeserializationContext): Boolean? {
        val code = json.asString
        return when (code) {
            "0" -> false
            "1" -> true
            else -> null
        }
    }
}