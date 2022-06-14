package com.pins.infinity.utility;

import com.pins.infinity.model.ContactsModel;

import java.util.Comparator;

/**
 * Created by shri.kant on 10-11-2016.
 */
public class CustomComparator implements Comparator<ContactsModel> {
    @Override
    public int compare(ContactsModel o1, ContactsModel o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
