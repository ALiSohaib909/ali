package com.pins.infinity.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shri.kant on 19-11-2016.
 */
public class ProfileBaseModel {
    @JsonProperty("message")
    private String message;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("response")
    private ResponseModel response;
    @JsonProperty("error")
    private Boolean error;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The message
     */
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return The code
     */
    @JsonProperty("code")
    public Integer getCode() {
        return code;
    }

    /**
     * @param code The code
     */
    @JsonProperty("code")
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * @return The response
     */
    @JsonProperty("response")
    public ResponseModel getResponse() {
        return response;
    }

    /**
     * @param response The response
     */
    @JsonProperty("response")
    public void setResponse(ResponseModel response) {
        this.response = response;
    }

    /**
     * @return The error
     */
    @JsonProperty("error")
    public Boolean getError() {
        return error;
    }

    /**
     * @param error The error
     */
    @JsonProperty("error")
    public void setError(Boolean error) {
        this.error = error;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
