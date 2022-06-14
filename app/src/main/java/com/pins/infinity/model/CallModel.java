package com.pins.infinity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bimalchawla on 24/8/17.
 */

public class CallModel {

    @JsonProperty("unique_hash")
    private String uniqueHash;
    @JsonProperty("time_of_call")
    private String time;
    @JsonProperty("duration")
    private String duration;
    @JsonProperty("call_type")
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

    @JsonProperty("time_of_call")
    public String getTime() {
        return time;
    }

    @JsonProperty("time_of_call")
    public void setTime(String time) {
        this.time = time;
    }

    @JsonProperty("duration")
    public String getDuration() {
        return duration;
    }
    @JsonProperty("duration")
    public void setDuration(String duration) {
        this.duration = duration;
    }

    @JsonProperty("call_type")
    public String getType() {
        return type;
    }

    @JsonProperty("call_type")
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
