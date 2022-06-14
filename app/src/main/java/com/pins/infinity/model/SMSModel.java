package com.pins.infinity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bimalchawla on 24/8/17.
 */

public class SMSModel {
    @JsonProperty("unique_hash")
    private String uniqueHash;
    @JsonProperty("time_of_sms")
    private String time;
    @JsonProperty("content")
    private String content;
    @JsonProperty("sms_type")
    private String type;
    @JsonProperty("from")
    private String from;
    @JsonProperty("to")
    private String to;

    @JsonProperty("unique_hash")
    public String getUniqueHash() {
        return uniqueHash;
    }

    @JsonProperty("unique_hash")
    public void setUniqueHash(String uniqueHash) {
        this.uniqueHash = uniqueHash;
    }

    @JsonProperty("time_of_sms")
    public String getTime() {
        return time;
    }

    @JsonProperty("time_of_sms")
    public void setTime(String time) {
        this.time = time;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("sms_type")
    public String getType() {
        return type;
    }

    @JsonProperty("sms_type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("from")
    public String getFrom() {
        return from;
    }

    @JsonProperty("from")
    public void setFrom(String from) {
        this.from = from;
    }

    @JsonProperty("to")
    public String getTo() {
        return to;
    }

    @JsonProperty("to")
    public void setTo(String to) {
        this.to = to;
    }
}
