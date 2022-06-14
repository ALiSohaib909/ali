package com.pins.infinity.utility

import android.net.Uri

/**
 * Created by Pavlo Melnyk on 16.01.2019.
 */

fun ussdToCallableUri(ussd: String) =
        Uri.parse(ussd.toCharArray().map { if (it == '#') Uri.encode("#") else it }
                .joinToString(""))

