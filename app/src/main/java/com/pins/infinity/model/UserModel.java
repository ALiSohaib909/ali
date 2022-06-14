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
public class UserModel {
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("device_country_code")
    private String deviceCountryCode;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("registered_date")
    private String registeredDate;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("device_longitude")
    private String deviceLongitude;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("verified")
    private Integer verified;
    @JsonProperty("image")
    private String image;
    @JsonProperty("subscription_id")
    private String subscriptionId;
    @JsonProperty("device_status")
    private String deviceStatus;
    @JsonProperty("email")
    private String email;
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("device_latitude")
    private String deviceLatitude;
    @JsonProperty("device_country")
    private String deviceCountry;
    @JsonProperty("birthday")
    private String birthday;
    @JsonProperty("imei")
    private String imei;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("country")
    private String country;
    @JsonProperty("int_code")
    private String intCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The lastName
     */
    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The last_name
     */
    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     * The deviceCountryCode
     */
    @JsonProperty("device_country_code")
    public String getDeviceCountryCode() {
        return deviceCountryCode;
    }

    /**
     *
     * @param deviceCountryCode
     * The device_country_code
     */
    @JsonProperty("device_country_code")
    public void setDeviceCountryCode(String deviceCountryCode) {
        this.deviceCountryCode = deviceCountryCode;
    }

    /**
     *
     * @return
     * The pin
     */
    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    /**
     *
     * @param pin
     * The pin
     */
    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }

    /**
     *
     * @return
     * The registeredDate
     */
    @JsonProperty("registered_date")
    public String getRegisteredDate() {
        return registeredDate;
    }

    /**
     *
     * @param registeredDate
     * The registered_date
     */
    @JsonProperty("registered_date")
    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    /**
     *
     * @return
     * The countryCode
     */
    @JsonProperty("country_code")
    public String getCountryCode() {
        return countryCode;
    }

    /**
     *
     * @param countryCode
     * The country_code
     */
    @JsonProperty("country_code")
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     *
     * @return
     * The deviceLongitude
     */
    @JsonProperty("device_longitude")
    public String getDeviceLongitude() {
        return deviceLongitude;
    }

    /**
     *
     * @param deviceLongitude
     * The device_longitude
     */
    @JsonProperty("device_longitude")
    public void setDeviceLongitude(String deviceLongitude) {
        this.deviceLongitude = deviceLongitude;
    }

    /**
     *
     * @return
     * The firstName
     */
    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The first_name
     */
    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The verified
     */
    @JsonProperty("verified")
    public Integer getVerified() {
        return verified;
    }

    /**
     *
     * @param verified
     * The verified
     */
    @JsonProperty("verified")
    public void setVerified(Integer verified) {
        this.verified = verified;
    }

    /**
     *
     * @return
     * The image
     */
    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    @JsonProperty("subscription_id")
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     *
     * @param image
     * The image
     */
    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    @JsonProperty("subscription_id")
    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    /**
     *
     * @return
     * The deviceStatus
     */
    @JsonProperty("device_status")
    public String getDeviceStatus() {
        return deviceStatus;
    }

    /**
     *
     * @param deviceStatus
     * The device_status
     */
    @JsonProperty("device_status")
    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
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

    /**
     *
     * @return
     * The accountId
     */
    @JsonProperty("account_id")
    public String getAccountId() {
        return accountId;
    }

    /**
     *
     * @param accountId
     * The account_id
     */
    @JsonProperty("account_id")
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     *
     * @return
     * The deviceLatitude
     */
    @JsonProperty("device_latitude")
    public String getDeviceLatitude() {
        return deviceLatitude;
    }

    /**
     *
     * @param deviceLatitude
     * The device_latitude
     */
    @JsonProperty("device_latitude")
    public void setDeviceLatitude(String deviceLatitude) {
        this.deviceLatitude = deviceLatitude;
    }

    /**
     *
     * @return
     * The deviceCountry
     */
    @JsonProperty("device_country")
    public String getDeviceCountry() {
        return deviceCountry;
    }

    /**
     *
     * @param deviceCountry
     * The device_country
     */
    @JsonProperty("device_country")
    public void setDeviceCountry(String deviceCountry) {
        this.deviceCountry = deviceCountry;
    }

    /**
     *
     * @return
     * The birthday
     */
    @JsonProperty("birthday")
    public String getBirthday() {
        return birthday;
    }

    /**
     *
     * @param birthday
     * The birthday
     */
    @JsonProperty("birthday")
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     *
     * @return
     * The imei
     */
    @JsonProperty("imei")
    public String getImei() {
        return imei;
    }

    /**
     *
     * @param imei
     * The imei
     */
    @JsonProperty("imei")
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     *
     * @return
     * The phone
     */
    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     * The phone
     */
    @JsonProperty("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @return
     * The country
     */
    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     * The country
     */
    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return
     * The intCode
     */
    @JsonProperty("int_code")
    public String getIntCode() {
        return intCode;
    }

    /**
     *
     * @param intCode
     * The int_code
     */
    @JsonProperty("int_code")
    public void setIntCode(String intCode) {
        this.intCode = intCode;
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
