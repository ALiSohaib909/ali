package com.pins.infinity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shri.kant on 28-11-2016.
 */
public class ContactsPhone {
    @JsonProperty("type")
    private String type;
    @JsonProperty("number")
    private String number;

    /**
     *
     * @return
     * The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The number
     */
    @JsonProperty("number")
    public String getNumber() {
        return number;
    }

    /**
     *
     * @param number
     * The number
     */
    @JsonProperty("number")
    public void setNumber(String number) {
        this.number = number;
    }

}
