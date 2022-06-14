package com.pins.infinity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bimalchawla on 16/3/17.
 */

public class PictureBaseModel {

    @JsonProperty("media_id")
    private String mediaId;
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("timestamp")
    private String time;
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("bucket")
    private String bucket;
    @JsonProperty("ext")
    private String extension;
    @JsonProperty("created_date")
    private String date;
    @JsonProperty("size")
    private String size;

    private String imageUrl;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
