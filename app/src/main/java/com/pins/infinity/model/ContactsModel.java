package com.pins.infinity.model;

import android.graphics.Bitmap;

import java.util.Comparator;

/**
 * Created by shri.kant on 10-11-2016.
 */
public class ContactsModel{
    private String name;
    private String mobileNo;
    private String emailId;
    private Bitmap pic;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }


//    @Override
//    public int compare(ContactsModel contactsModel, ContactsModel t1) {
//        return contactsModel.getName().compareTo(t1.getName());
//    }
}
