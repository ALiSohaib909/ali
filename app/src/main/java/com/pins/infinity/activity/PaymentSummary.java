package com.pins.infinity.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abc on 9/19/2017.
 */

public class PaymentSummary extends AppCompatActivity implements AdapterView.OnItemSelectedListener,ApiResponseListener {

    private ImageView back;
    private Button continue_button;
    Spinner spinner;
    SubscriptionPlanModel object;
    TextView plan_name,plan_rate,vat,total_rate,recovery_plan;
    double total = 0;
    View recovery_plan_header_line;
    LinearLayout recovery_plan_view;
    Boolean recovery_enable,dialog_shown = false;
    private ProgressDialog progressDialog;
    private String token = "";
    private String userId = "";
    String plan_duration = "month";
    Double recovery_amount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.payment_summary);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        recovery_enable = bundle.getBoolean("recovery_selection");
        recovery_amount = bundle.getDouble("recovery_amount");

        recovery_plan_view = (LinearLayout)findViewById(R.id.recovery_plan_view);
        recovery_plan_header_line = (View)findViewById(R.id.recovery_plan_header_line);
        if(recovery_enable)
        {
            recovery_plan_view.setVisibility(View.VISIBLE);
            recovery_plan_header_line.setVisibility(View.VISIBLE);
        }
        else
        {
            recovery_plan_view.setVisibility(View.GONE);
            recovery_plan_header_line.setVisibility(View.GONE);
        }

        plan_name = (TextView) findViewById(R.id.plan_name);
        plan_rate = (TextView) findViewById(R.id.plan_rate);
        vat = (TextView) findViewById(R.id.vat);
        total_rate = (TextView) findViewById(R.id.total_rate);
        recovery_plan = (TextView) findViewById(R.id.recovery_plan);
        recovery_plan.setText("₦"+recovery_amount);
        continue_button = (Button) findViewById(R.id.continue_button);

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(recovery_enable || dialog_shown || plan_name.getText().toString().equalsIgnoreCase("PREMIUM") )
                {
                    try {
                        getTransactionId();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                else
                showDialog();




            }
        });




        object= (SubscriptionPlanModel)bundle.getSerializable("object");

        plan_name.setText(object.getPlan_name());

        spinner = (Spinner) findViewById(R.id.spinner);


        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("1 Month Subscription");
        categories.add("1 Year Subscription");

       /* if(object.getPlan_name().contains("BASIC"))
        {
            categories.add("1 Year Subscription");
        }
        else {
            categories.add("1 Month Subscription");
            categories.add("1 Year Subscription");
        }
*/

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView) parent.getChildAt(0)).setTextSize(10);

        if(String.valueOf(spinner.getSelectedItem()).contains("Year")) {
            plan_rate.setText("₦" + object.getPlan_price());
            vat.setText("₦"+getPercentage(5,object.getPlan_price()));
            if(recovery_enable)
            {
                total = object.getPlan_price()+ getPercentage(5,object.getPlan_price())+ recovery_amount;
            }
            else
                total = object.getPlan_price()+ getPercentage(5,object.getPlan_price());
            total_rate.setText("₦"+total);
            plan_duration = "year";
        }
        else {
            plan_rate.setText("₦" + object.getPlan_price_monthly());
            vat.setText("₦"+getPercentage(5,object.getPlan_price_monthly()));
            if(recovery_enable)
            {
                total = object.getPlan_price_monthly()+ getPercentage(5,object.getPlan_price_monthly())+ recovery_amount;
            }
            else
                total = object.getPlan_price_monthly()+ getPercentage(5,object.getPlan_price_monthly());

            total_rate.setText("₦"+total);
            plan_duration = "month";
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public float getPercentage(int n, double total) {

        if(recovery_enable)
        {
            total = total + recovery_amount;
        }
        float proportion = ((float) n) * ((float) total);
        return proportion / 100;
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(PaymentSummary.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.recovery_dialog);

        Button with_recovery = (Button) dialog.findViewById(R.id.with_recovery);
        Button without_recovery = (Button) dialog.findViewById(R.id.without_recovery);

        with_recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recovery_plan_view.setVisibility(View.VISIBLE);
                recovery_plan_header_line.setVisibility(View.VISIBLE);

                total = total + recovery_amount;

                total_rate.setText("₦"+total);

                dialog_shown = true;
                recovery_enable = true;
                dialog.dismiss();

            }
        });

        without_recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_shown = true;
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    private void getTransactionId() {
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

        HashMap<String, String> params = new HashMap<>();
        params.put("plan", object.getPlan_name().toLowerCase());
        params.put("duration", plan_duration);
        params.put("app", "mobile");
        if(recovery_enable)
            params.put("recovery","1");
        else
            params.put("recovery","0");


        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);

        String url = "payment/init/"+userId;
        ApiCall.getInstance().makePostRequest(PaymentSummary.this, url, params, header, this, 211);

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

                if (jsonObject != null)
                {

                    JSONObject responseObject = jsonObject.getJSONObject("response");
                    String trans_id = responseObject.getString("trans_id");
                    int total_in_unit = responseObject.getInt("total_in_unit");
                    Intent intent = new Intent(PaymentSummary.this,PaymentSelection.class);
                    intent.putExtra("total_amount",Integer.valueOf((int) total));
                    intent.putExtra("plan_name",object.getPlan_name());
                    intent.putExtra("trans_id",trans_id);
                    intent.putExtra("total_in_unit",total_in_unit);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }

                else
                {
                    Utility.showToast(this, "Please try again later !!");
                }



            } catch (JSONException e) {
                e.printStackTrace();
                Utility.showToast(this, "Please try again later !!");
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
    }
}
