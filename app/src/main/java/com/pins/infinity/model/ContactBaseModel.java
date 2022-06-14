package com.pins.infinity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shri.kant on 28-11-2016.
 */
public class ContactBaseModel {
    @JsonProperty("unique_hash")
    private String uniqueHash;
    @JsonProperty("title")
    private String title;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("organisation")
    private String organisation;
    @JsonProperty("emails")
    private List<ContactEmails> emails = new ArrayList<ContactEmails>();
    @JsonProperty("phones")
    private List<ContactsPhone> phones = new ArrayList<ContactsPhone>();

    /**
     *
     * @return
     * The uniqueHash
     */
    @JsonProperty("unique_hash")
    public String getUniqueHash() {
        return uniqueHash;
    }

    /**
     *
     * @param uniqueHash
     * The unique_hash
     */
    @JsonProperty("unique_hash")
    public void setUniqueHash(String uniqueHash) {
        this.uniqueHash = uniqueHash;
    }

    /**
     *
     * @return
     * The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

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
     * The organisation
     */
    @JsonProperty("organisation")
    public String getOrganisation() {
        return organisation;
    }

    /**
     *
     * @param organisation
     * The organisation
     */
    @JsonProperty("organisation")
    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    /**
     *
     * @return
     * The emails
     */
    @JsonProperty("emails")
    public List<ContactEmails> getEmails() {
        return emails;
    }

    /**
     *
     * @param emails
     * The emails
     */
    @JsonProperty("emails")
    public void setEmails(List<ContactEmails> emails) {
        this.emails = emails;
    }

    /**
     *
     * @return
     * The phones
     */
    @JsonProperty("phones")
    public List<ContactsPhone> getPhones() {
        return phones;
    }

    /**
     *
     * @param phones
     * The phones
     */
    @JsonProperty("phones")
    public void setPhones(List<ContactsPhone> phones) {
        this.phones = phones;
    }

}
