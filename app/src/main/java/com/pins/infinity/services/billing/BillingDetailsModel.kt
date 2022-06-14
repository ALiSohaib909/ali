package com.pins.infinity.services.billing

import com.android.billingclient.api.SkuDetails

/**
 * Created by Pavlo Melnyk on 2018-12-18.
 */

class BillingDetailsModel {
    var skuDetailsList: List<SkuDetails>? = null
    var billingManager: BillingManager? = null
    var shouldShowGooglePayment: Boolean = false
    var isSubscriptionValid: Boolean = false

}

