package com.pins.infinity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shri.kant on 28-11-2016.
 */
public class ContactEmails {
    @JsonProperty("type")
    private String type;
    @JsonProperty("email")
    private String email;

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
     * The email
     */
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

}
