package com.pins.infinity.model;

import io.realm.RealmObject;

/**
 * Created by bimalchawla on 12/2/17.
 */

public class ScanResultModel extends RealmObject {

    String name;
    String number;
    int image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
