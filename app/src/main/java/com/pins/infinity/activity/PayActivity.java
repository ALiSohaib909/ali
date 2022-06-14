package com.pins.infinity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pins.infinity.BuildConfig;
import com.pins.infinity.R;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.model.SubscriptionPlanModel;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

import static com.pins.infinity.database.SettingsManagerImpl.KEY_PAYED_BY_CARD;

/**
 * Created by bimalchawla on 9/7/17.
 */

public class PayActivity extends Activity implements ApiResponseListener{
    // To get started quickly, change this to your heroku deployment of
    // https://github.com/PaystackHQ/sample-charge-card-backend
    // Step 1. Visit https://github.com/PaystackHQ/sample-charge-card-backend
    // Step 2. Click "Deploy to heroku"
    // Step 3. Login with your heroku credentials or create a free heroku account
    // Step 4. Provide your secret key and an email with which to start all test transactions
    // Step 5. Copy the url generated by heroku (format https://some-url.heroku-app.com) into the space below
    String backend_url = "https://vast-springs-49876.herokuapp.com";

    EditText mEditCardNum;
    EditText mEditCVC;
    EditText mEditExpiryMonth;
    EditText mEditExpiryYear;

    //TextView mTextError;
   // TextView mTextBackendMessage;

    Card card;

    ProgressDialog dialog;
   // private TextView mTextReference;
    private Charge charge;
    private Transaction transaction;
    int amount;
    String plan_name;
    TextView total_amount;
    View transaction_failed,transaction_successfull;
    RelativeLayout card_layout;
    private ImageView back;
    private Button home_button,tryagain;
    private ProgressDialog progressDialog;
    private String token = "";
    private String userId = "";
    private String trans_id = "";
    int total_in_unit = 0;

    private static final int CURRENCY_UNIT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        trans_id = bundle.getString("trans_id");
        amount = (int)bundle.get("total_amount");
        plan_name = bundle.getString("plan_name");
        total_in_unit = bundle.getInt("total_in_unit");
        total_amount = (TextView) findViewById(R.id.total_amount);
        total_amount.setText(amount+"");


        transaction_failed = (View)findViewById(R.id.transaction_failed);
        transaction_successfull = (View)findViewById(R.id.transaction_successfull);
        card_layout = (RelativeLayout) findViewById(R.id.card_layout);

        transaction_failed.setVisibility(View.GONE);
        transaction_successfull.setVisibility(View.GONE);
        card_layout.setVisibility(View.VISIBLE);


        mEditCardNum = (EditText) findViewById(R.id.edit_card_number);
        mEditCVC = (EditText) findViewById(R.id.edit_cvc);
        mEditExpiryMonth = (EditText) findViewById(R.id.edit_expiry_month);
        mEditExpiryYear = (EditText) findViewById(R.id.edit_expiry_year);

        mEditCardNum.addTextChangedListener(new FourDigitCardFormatWatcher());

        home_button = (Button) findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        tryagain = (Button) findViewById(R.id.tryagain);
        tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* mEditCardNum.setText("");
                mEditCVC.setText("");
                mEditExpiryMonth.setText("");
                mEditExpiryYear.setText("");*/

                transaction_failed.setVisibility(View.GONE);
                transaction_successfull.setVisibility(View.GONE);
                card_layout.setVisibility(View.VISIBLE);


            }
        });

        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        Button mButtonPerformTransaction = (Button) findViewById(R.id.button_perform_transaction);

      /*  mTextError = (TextView) findViewById(R.id.textview_error);
        mTextBackendMessage = (TextView) findViewById(R.id.textview_backend_message);
        mTextReference = (TextView) findViewById(R.id.textview_reference);*/

        //set click listener
        mButtonPerformTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate form
                validateCardForm();
                //check card validity
                if (card != null && card.isValid()) {
                    dialog = new ProgressDialog(PayActivity.this);
                    dialog.setMessage("Performing transaction... please wait");
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(false);

                    dialog.show();
                    try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditExpiryYear.getWindowToken(), 0);
                    }
                    catch (Exception e) {}


                    try {
                        startAFreshCharge();
                    } catch (Exception e) {
                        transaction_failed.setVisibility(View.VISIBLE);
                        transaction_successfull.setVisibility(View.GONE);
                        card_layout.setVisibility(View.GONE);
                     //   mTextError.setText(String.format("An error occurred while charging card: %s %s", e.getClass().getSimpleName(), e.getMessage()));
                    }

                }
            }
        });
    }

    private void startAFreshCharge() {

        String email="";

        try {
            String response = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                email =profileBaseModel.getResponse().getUser().getEmail();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // initialize the charge
        charge = new Charge();
        charge.setCard(card);
        charge.setReference(trans_id);
        charge.setAmount(amount*CURRENCY_UNIT);
        charge.setEmail(email);
        chargeCard();




    }

    /**
     * Method to validate the form, and set errors on the edittexts.
     */
    private void validateCardForm() {
        //validate fields
        String cardNum = mEditCardNum.getText().toString().trim();
        cardNum = cardNum.replace(" ", "");

        if (isEmpty(cardNum)) {
            mEditCardNum.setError("Empty card number");
            return;
        }

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, 0, 0, "").build();
        if (!card.validNumber()) {
            mEditCardNum.setError("Invalid card number");
            return;
        }

        //validate cvc
        String cvc = mEditCVC.getText().toString().trim();
        if (isEmpty(cvc)) {
            mEditCVC.setError("Empty cvc");
            return;
        }
        //update the cvc field of the card
        card.setCvc(cvc);

        //check that it's valid
        if (!card.validCVC()) {
            mEditCVC.setError("Invalid cvc");
            return;
        }

        //validate expiry month;
        String sMonth = mEditExpiryMonth.getText().toString().trim();
        int month = -1;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        if (month < 1) {
            mEditExpiryMonth.setError("Invalid month");
            return;
        }

        card.setExpiryMonth(month);

        String sYear = mEditExpiryYear.getText().toString().trim();
        int year = -1;
        try {
            year = Integer.parseInt(sYear);
        } catch (Exception ignored) {
        }

        if (year < 1) {
            mEditExpiryYear.setError("invalid year");
            return;
        }

        card.setExpiryYear(year);

        //validate expiry
        if (!card.validExpiryDate()) {
            mEditExpiryMonth.setError("Invalid expiry");
            mEditExpiryYear.setError("Invalid expiry");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing()){
            dialog.dismiss();
        }
        dialog = null;
    }

    private void chargeCard() {
        transaction = null;
        PaystackSdk.chargeCard(PayActivity.this, charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(Transaction transaction) {
                dismissDialog();
                System.out.println("PINSAPP chargeCard onSuccess");
          //      mTextError.setText(" ");
              //  Toast.makeText(PayActivity.this, transaction.getReference(), Toast.LENGTH_LONG).show();
                completeTransaction();
             //   updateTextViews(transaction.getReference());

              //  new verifyOnServer().execute(transaction.getReference());

            }

            // This is called only before requesting OTP
            // Save reference so you may send to server if
            // error occurs with OTP
            // No need to dismiss dialog
            @Override
            public void beforeValidate(Transaction transaction) {
                Toast.makeText(PayActivity.this, transaction.getReference(), Toast.LENGTH_LONG).show();
               // updateTextViews(transaction.getReference());
                System.out.println("PINSAPP chargeCard beforeValidate");
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                Log.v("PINSAPP", "chargeCard onError" + error.getMessage());
                System.out.println("PINSAPP");
                if (error instanceof ExpiredAccessCodeException) {
                    startAFreshCharge();
                    chargeCard();
                    return;
                }

                dismissDialog();

                if (transaction.getReference() != null) {
                    Toast.makeText(PayActivity.this, transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    transaction_failed.setVisibility(View.VISIBLE);
                    transaction_successfull.setVisibility(View.GONE);
                    card_layout.setVisibility(View.GONE);
                 //   mTextError.setText(String.format("%s  concluded with error: %s %s", transaction.getReference(), error.getClass().getSimpleName(), error.getMessage()));
                    //new verifyOnServer().execute(transaction.getReference());
                } else {
                    Toast.makeText(PayActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    transaction_failed.setVisibility(View.VISIBLE);
                    transaction_successfull.setVisibility(View.GONE);
                    card_layout.setVisibility(View.GONE);
                 //   mTextError.setText(String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()));
                }
               // updateTextViews(transaction.getReference());
            }

        });
    }

    private void dismissDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
//
//    private void updateTextViews() {
//        if (transaction.getReference() != null) {
//            mTextReference.setText(String.format("Reference: %s", transaction.getReference()));
//        } else {
//            mTextReference.setText("No transaction");
//        }
//    }

  /*  private void updateTextViews(String trans) {
        if (trans != null) {
         //   mTextReference.setText(String.format("Reference: %s", trans));
        } else {
         //   mTextReference.setText("No transaction");
        }
    }*/

    private boolean isEmpty(String s) {
        return s == null || s.length() < 1;
    }

    @Override
    public void onResponse(String response, int requestCode) throws IOException {

        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            progressDialog = null;
        } finally {
            progressDialog = null;
        }

        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject != null) {
                    JSONObject responseJson = jsonObject.getJSONObject(ApiConstants.RESPONSE);
                    int code = jsonObject.getInt(ApiConstants.CODE);
                    if(code == ApiConstants.CODE_SUCCESS)
                    {
                        transaction_failed.setVisibility(View.GONE);
                        transaction_successfull.setVisibility(View.VISIBLE);
                        card_layout.setVisibility(View.GONE);
                        AppSharedPrefrence.putBoolean(this, KEY_PAYED_BY_CARD, true);
                    }
                    else
                    {
                        Utility.showToast(PayActivity.this, responseJson.getString(ApiConstants.MESSAGE));
                        transaction_successfull.setVisibility(View.GONE);
                        card_layout.setVisibility(View.GONE);
                    }
                }

                else
                {
                    transaction_successfull.setVisibility(View.GONE);
                    card_layout.setVisibility(View.GONE);
                }



            } catch (JSONException e) {
                e.printStackTrace();
                transaction_successfull.setVisibility(View.GONE);
                card_layout.setVisibility(View.GONE);

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


        if (error != null) {
            try {

                JSONObject jsonObject = new JSONObject(error);
                if (jsonObject != null) {
                    String message = jsonObject.getString("message");
                    Utility.showToast(this, message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        transaction_failed.setVisibility(View.VISIBLE);
        transaction_successfull.setVisibility(View.GONE);
        card_layout.setVisibility(View.GONE);
    }


    private void completeTransaction() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

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

        //String url = "http://private-anon-5ec4da1eb1-pin1.apiary-mock.com"+ ApiConstants.DIAGNOSE  + "/"+ userId + "/" + Utility.getImei(this);
        String url = "payment/complete/"+userId+"/"+trans_id;
        // String url = ApiConstants.urlBuilderCotactsWithTwoPara(ApiConstants.API_ADDRESS.DIAGNOSE.path, null, userId, Utility.getImei(this));
        ApiCall.getInstance().makePutRequestWithUrl(PayActivity.this, url, null, header, this, 211,null);

    }

    /**
     * Formats the watched EditText to a credit card number
     */
    public static class FourDigitCardFormatWatcher implements TextWatcher {

        // Change this to what you want... ' ', '-' etc..
        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Remove spacing char
            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            // Insert char where needed.
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
        }
    }



}