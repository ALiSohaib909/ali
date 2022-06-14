package com.pins.infinity.model;

import io.realm.RealmObject;

/**
 * Created by bimalchawla on 15/2/17.
 */

public class ScanModel extends RealmObject {

    String date;
    String virus;
    String totalApps, goodApps, infectedApps;


    public String getVirus() {
        return virus;
    }

    public void setVirus(String virus) {
        this.virus = virus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalApps() {
        return totalApps;
    }

    public void setTotalApps(String totalApps) {
        this.totalApps = totalApps;
    }

    public String getGoodApps() {
        return goodApps;
    }

    public void setGoodApps(String goodApps) {
        this.goodApps = goodApps;
    }

    public String getInfectedApps() {
        return infectedApps;
    }

    public void setInfectedApps(String infectedApps) {
        this.infectedApps = infectedApps;
    }
}
