package com.pins.infinity.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.R;
import com.pins.infinity.adapters.ContactsAdapter;
import com.pins.infinity.adapters.DividerItemDecoration;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.ContactBaseModel;
import com.pins.infinity.model.ContactEmails;
import com.pins.infinity.model.ContactsModel;
import com.pins.infinity.model.ContactsPhone;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.CustomComparator;
import com.pins.infinity.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by shri.kant on 10-11-2016.
 */
public class ActivityContacts extends AppCompatActivity implements View.OnClickListener, ApiResponseListener {
    RecyclerView recyclerView;
    private ImageView back;
    private List<ContactsModel> contactsModelList = new ArrayList<>();
    private List<ContactsModel> apiContactsModelList = new ArrayList<>();
    private List<ContactBaseModel> backUpList = new ArrayList<>();
    private ContactsAdapter mAdapter;
    private TextView numberContacts;
    private ProgressDialog progressDialog;
    private String token = "";
    private String userId = "";
    private String imei = "";
    private Button mBackUp;
    private Button retrieveButton;
    private boolean isRetrieveCalled;
    private int contactCount = 0, remainingCount = 0, counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        try {
            String response = AppSharedPrefrence.getString(ActivityContacts.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        imei = Utility.getImei(this);
        initIds();
        initClickListners();
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });


        if (progressDialog != null && progressDialog.isShowing()) {

        } else {
            progressDialog = new ProgressDialog(ActivityContacts.this);
            progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
            //loading phone contacts in case device is offline
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    readContacts();
                }
            }, 1000);

    }

    private void initIds() {
        mBackUp = (Button) findViewById(R.id.cont_backup);
        retrieveButton = (Button) findViewById(R.id.retrive);
        numberContacts = (TextView) findViewById(R.id.number_contacts);
        numberContacts.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.cont_recycler_view);

    }

    private void initClickListners() {
        mBackUp.setOnClickListener(this);
        retrieveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cont_backup:

                if (null != backUpList && !backUpList.isEmpty()) {
                    if (progressDialog != null && progressDialog.isShowing()) {

                    } else {
                        progressDialog = new ProgressDialog(ActivityContacts.this);
                        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                    contactCount = (backUpList.size() / 20);
                    syncContacts(backUpList);
                } else {
                    Utility.showToast(ActivityContacts.this, "No contact found for backup");
                }

                break;

            case R.id.retrive:
                if (progressDialog != null && progressDialog.isShowing()) {

                } else {
                    progressDialog = new ProgressDialog(ActivityContacts.this);
                    progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }

                isRetrieveCalled = true;
                if (Utility.isInternetOn(ActivityContacts.this)) {
                    //loading contacts from api if device is online
                    getContacts();
                } else {
                    //loading phone contacts in case device is offline
                    Utility.showToast(this, getResources().getString(R.string.error_no_internet_connection));
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);

    }


    public void readContacts() {
        StringBuffer sb = new StringBuffer();
        sb.append("......Contact Details.....");
        ContentResolver cr = getContentResolver();
        contactsModelList = new ArrayList<>();
        backUpList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();

        String[] projections = {ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_URI
                , ContactsContract.Data.MIMETYPE
                , ContactsContract.Contacts._ID
                , ContactsContract.CommonDataKinds.Email.TYPE,
                ContactsContract.CommonDataKinds.Email.LABEL
                , ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
                ContactsContract.CommonDataKinds.Phone.LABEL};
        Cursor cur = cr.query(
                ContactsContract.Data.CONTENT_URI,
                projections,
                // null,
                ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                ContactsContract.Data.DISPLAY_NAME);

//        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
//                null, null, null);
        String emailContact = null;
        String emailType = null;
        Bitmap bitmap = null;
        System.out.println("cursor count  " + cur.getCount());
        for (int j = 0; j < cur.getCount(); j++) {

            cur.moveToPosition(j);
            ContactsModel contactsModel = new ContactsModel();
            ContactBaseModel baseModel = new ContactBaseModel();
            // get the contact's information
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if (!nameList.contains(name)) {
                String image_uri = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                System.out.println("id " + id + " name : " + name);
                if (name != null && name.length() > 0) {
                    baseModel.setFirstName(name);
                    baseModel.setLastName("");
                    baseModel.setUniqueHash(id);
                    baseModel.setOrganisation("PINS");
                    baseModel.setTitle("Contacts");
                }
                nameList.add(name);
                contactsModel.setName(name);
                Integer hasPhone = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                // get email data
                if (cur.getString(cur.getColumnIndex(ContactsContract.Data.MIMETYPE)).equalsIgnoreCase(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                    List<ContactEmails> emailsList = new ArrayList<>();
                    String email = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                    String type = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                    ContactEmails contactEmails = new ContactEmails();
                    contactEmails.setEmail(email);
                    contactEmails.setType(type);
                    emailsList.add(contactEmails);
                    if (emailsList != null) {
                        baseModel.setEmails(emailsList);
                        Log.d("email size", emailsList.size() + "");
                    }
                    if (email != null && email.length() > 0) {
                        contactsModel.setEmailId(email + "," + emailType);

                        sb.append("\nEmail:" + emailContact + "Email type:" + emailType);
                        System.out.println("Email " + emailContact + " Email Type : " + emailType);
                    }
                }
                if (hasPhone > 0) {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.Data.MIMETYPE)).equalsIgnoreCase(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                        List<ContactsPhone> phoneList = new ArrayList<>();
                        String phone = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int type = cur.getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                        ContactsPhone phoneData = new ContactsPhone();
                        phoneData.setNumber(phone);
                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                phoneData.setType("home");
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                phoneData.setType("mobile");
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                phoneData.setType("work");
                                break;
                        }

                        phoneList.add(phoneData);
                        if (phone != null && phone.length() > 0) {
                            contactsModel.setMobileNo(phone);
                            sb.append("\n Phone number:" + phone);
                            System.out.println("phone" + phone);
                        }
                        if (!Utility.checkString(contactsModel.getMobileNo()) && null != phoneList) {
                            for (int i = 0; i < phoneList.size(); i++) {
                                if (Utility.checkString(phoneList.get(i).getNumber())) {
                                    contactsModel.setMobileNo(phoneList.get(i).getNumber());
                                    System.out.println("phone22222" + phone);
                                }
                            }
                        }
                        baseModel.setPhones(phoneList);
                    }
                }

                if (image_uri != null && null != Uri.parse(image_uri)) {
                    System.out.println("uri   " + Uri.parse(image_uri));
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image_uri));
                        contactsModel.setPic(bitmap);
                    } catch (Exception e) {

                    }
                }
                contactsModelList.add(contactsModel);
                backUpList.add(baseModel);

                Log.d("size", "" + contactsModelList.size());

            }
        }
            // clean up cursor
            cur.close();

            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (IllegalArgumentException e) {
                progressDialog = null;
            } finally {
                progressDialog = null;
            }
            numberContacts.setText("Total Contacts : "+contactsModelList.size());
            numberContacts.setVisibility(View.VISIBLE);
            Collections.sort(contactsModelList, new CustomComparator());
            mAdapter = new ContactsAdapter(contactsModelList);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(mAdapter);
    }

    private void syncContacts(List<ContactBaseModel> baseModel) {

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        int length = 0;

        try {
            System.out.println("remaining   " + remainingCount + "   " + baseModel.size());
            if ((baseModel.size() / ((remainingCount + 1) * 20)) > 0) {
                length = 20;
            } else {
                length = baseModel.size() - ((remainingCount) * 20);
            }
            for (int count = (remainingCount * 20); count < ((remainingCount * 20) + length); count++) {
                if ((remainingCount * 20) == count) {
                    jsonInString = "[" + mapper.writeValueAsString(baseModel.get(count)) + ",";
                } else if (count == ((remainingCount * 20) + length) - 1) {
                    jsonInString = jsonInString + mapper.writeValueAsString(baseModel.get(count)) + "]";
                } else {
                    jsonInString = jsonInString + mapper.writeValueAsString(baseModel.get(count)) + ",";
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            String response = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                token = profileBaseModel.getResponse().getToken();
                userId = profileBaseModel.getResponse().getUser().getAccountId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);

        ApiCall.getInstance().makePostRequestContact(this, ApiConstants.API_ADDRESS.SYNCH_CONTACTS.path, jsonInString, header, this, 101, userId, imei);

    }

    void getContacts() {
        try {
            String response = AppSharedPrefrence.getString(ActivityContacts.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                token = profileBaseModel.getResponse().getToken();
                userId = profileBaseModel.getResponse().getUser().getAccountId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("account_id", userId);
//        params.put("imei", imei);
        params.put("start", String.valueOf(counter));

        if(counter == 0) {
            apiContactsModelList = new ArrayList<>();
        }

        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);

        String url = ApiConstants.BASE_URL + ApiConstants.FETCH_CONTACTS + userId + "/" + imei +"?start="+ counter +"&limit=100";
        ApiCall.getInstance().makeGetRequestWithUrl(ActivityContacts.this, url, params, header, this, 201);
    }

    @Override
    public void onResponse(String response, int requestCode) throws IOException {
        System.out.println("response    "+response);
        if (requestCode == 101) {
            int res = Integer.parseInt(response);
            if (res != 0 && res == 200) {
                if (contactCount == remainingCount) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (IllegalArgumentException e) {
                        progressDialog = null;
                    } finally {
                        progressDialog = null;
                    }
                    Utility.showToast(ActivityContacts.this, "Sync successful");

                    getContacts();
                } else {
                    remainingCount++;
                    syncContacts(backUpList);
                }

            } else {
                try {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (IllegalArgumentException e) {
                    progressDialog = null;
                } finally {
                    progressDialog = null;
                }
            }
        } else if (requestCode == 201) {

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject != null) {
                        String message = jsonObject.optString("message");
                        JSONObject data = jsonObject.optJSONObject("response");

                        if (!jsonObject.getString("error").equalsIgnoreCase("true")) {
                            JSONObject contacts = data.optJSONObject("contacts");
                           JSONArray array = contacts.optJSONArray("list");
                            int size = contacts.optInt("total");
                            Log.d("list", size + "");
                            int remaining = 0;
                           if(size > ((counter+100))) {
                               remaining = 100;
                           } else {
                               remaining = size - (counter);
                           }
                            for (int count = 0; count < remaining; count++) {
                                ContactsModel model = new ContactsModel();
                                JSONObject object = array.optJSONObject(count);
                                System.out.println("object  " + count + "    /   " + object.toString());
                                model.setName(object.optString("first_name"));
                                JSONArray phoneArray = object.optJSONArray("phones");
                                for (int nextCount = 0; nextCount < phoneArray.length(); nextCount++) {
                                    JSONObject phoneObject = phoneArray.optJSONObject(nextCount);
                                    model.setMobileNo(phoneObject.optString("number"));
                                }
                                apiContactsModelList.add(model);

                            }

                            counter = counter + 100;
                            if(size > counter) {
                            //load next contacts upto next 100 numbers
                                getContacts();
                            } else{
                                try {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                } catch (IllegalArgumentException e) {
                                    progressDialog = null;
                                } finally {
                                    progressDialog = null;
                                }

                                if (isRetrieveCalled) {
                                    isRetrieveCalled = false;

                                    if (null == contactsModelList || contactsModelList.isEmpty()) {
                                        fillContactsInPhone(apiContactsModelList);
                                    } else {
                                        List<ContactsModel> newContactList = new ArrayList<>();
                                        if (null != apiContactsModelList && !apiContactsModelList.isEmpty())
                                            for (ContactsModel model : apiContactsModelList) {
                                                for (ContactsModel previousModel : contactsModelList) {
                                                    if (Utility.checkString(previousModel.getName()) && Utility.checkString(model.getName())
                                                            && Utility.checkString(previousModel.getMobileNo()) && Utility.checkString(model.getMobileNo())
                                                            && !previousModel.getName().contains(model.getName())
                                                            && !previousModel.getMobileNo().contains(model.getMobileNo())) {
                                                        newContactList.add(model);
                                                        System.out.println("adding   " + model.toString());
                                                        break;
                                                    }
                                                }
                                            }
                                        fillContactsInPhone(newContactList);
                                    }

                                } else {
                                    Utility.showToast(ActivityContacts.this, message);

                                    numberContacts.setText("Total Contacts : " + size);
                                    numberContacts.setVisibility(View.VISIBLE);
                                    Collections.sort(apiContactsModelList, new CustomComparator());
                                    mAdapter = new ContactsAdapter(apiContactsModelList);
                                    recyclerView.setHasFixedSize(true);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                                    recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

                                    recyclerView.setAdapter(mAdapter);

                                }
                                counter = 0;

                            }

                        } else {
                            Utility.showToast(ActivityContacts.this, message);
                        }
                    }
                } catch (JSONException e) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (IllegalArgumentException exe) {
                        progressDialog = null;
                    } finally {
                        progressDialog = null;
                    }
                    e.printStackTrace();
                } catch (Exception ex) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (IllegalArgumentException e) {
                        progressDialog = null;
                    } finally {
                        progressDialog = null;
                    }
                    ex.printStackTrace();
                }
            } else {
                try {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (IllegalArgumentException e) {
                    progressDialog = null;
                } finally {
                    progressDialog = null;
                }
            }
        }
    }

    @Override
    public void onError(String error, int errorCode, int requestCode) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            progressDialog = null;
        } finally {
            progressDialog = null;
        }

        if(requestCode == 201) {
            //loading phone contacts in case device is offline
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    readContacts();
                }
            }, 1000);

        }
        if (error != null) {
            try {
                if (errorCode == 402) {
                    AppSharedPrefrence.clearAllPrefs(ActivityContacts.this);
                    Intent login = new Intent(ActivityContacts.this, ActivityLogin.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(login);
                    finish();
                } else {

                    JSONObject jsonObject = new JSONObject(error);

                    if (jsonObject != null) {
                        String message = jsonObject.getString("message");
                        Utility.showToast(this, message);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillContactsInPhone(List<ContactsModel> list) {
        for (ContactsModel model : list) {
            ContentValues values = new ContentValues();
            values.put(Contacts.People.NUMBER, model.getMobileNo());
            values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
            values.put(Contacts.People.LABEL, model.getName());
            values.put(Contacts.People.NAME, model.getName());
            Uri dataUri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
            Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
            values.clear();
            values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
            values.put(Contacts.People.NUMBER, model.getMobileNo());
            updateUri = getContentResolver().insert(updateUri, values);
        }
    }
}