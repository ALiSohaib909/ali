package com.pins.infinity.model;

import java.io.Serializable;

/**
 * Created by abc on 9/19/2017.
 */

public class SubscriptionPlanModel implements Serializable{

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public int getPlan_price() {
        return plan_price;
    }

    public void setPlan_price(int plan_price) {
        this.plan_price = plan_price;
    }

    public Boolean getPlan_status() {
        return plan_status;
    }

    public void setPlan_status(Boolean plan_status) {
        this.plan_status = plan_status;
    }

    String plan_name;
    int plan_price;
    Boolean plan_status;

    public int getPlan_price_monthly() {
        return plan_price_monthly;
    }

    public void setPlan_price_monthly(int plan_price_monthly) {
        this.plan_price_monthly = plan_price_monthly;
    }

    int plan_price_monthly;


}
