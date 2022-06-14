package com.pins.infinity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.Iterator;
import java.util.Map;

/**
 * Created by abc on 9/19/2017.
 */

public class SubscriptionPlanSelection extends AppCompatActivity implements ApiResponseListener {

    private ImageView back;
    private Button continue_button;
    ListView plan_list;
    ArrayList<SubscriptionPlanModel> plans = new ArrayList<>();
    CustomBaseAdapter adapter;
    SubscriptionPlanModel selected_model;
    CheckBox recoveryPlan;
    RelativeLayout recovery_view;
    private ProgressDialog progressDialog;
    private String token = "";
    private String userId = "";
    TextView recoverydescription;
    Boolean isRecoveryAvailable = false;
    double recovery_amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.choose_your_plan);

        recovery_view = (RelativeLayout) findViewById(R.id.recovery_view);
        recovery_view.setVisibility(View.GONE);
        recoveryPlan = (CheckBox) findViewById(R.id.recoveryPlan);

        recoverydescription = (TextView) findViewById(R.id.recoverydescription);

        try {
            plans.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        plan_list = (ListView) findViewById(R.id.plan_list);

        getSubscriptionPlans();

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        continue_button = (Button) findViewById(R.id.continue_button);
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(SubscriptionPlanSelection.this, PaymentSummary.class);

                    Bundle bundle = new Bundle();
                    bundle.putBoolean("recovery_selection",recoveryPlan.isChecked());
                    bundle.putDouble("recovery_amount",recovery_amount);
                    bundle.putSerializable("object", selected_model);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
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
                    try {

                        JSONObject responseObject = jsonObject.getJSONObject("response");
                        JSONObject basic = responseObject.getJSONObject("basic");

                        SubscriptionPlanModel m = null;
                        Iterator<String> keysIterator = basic.keys();
                        while (keysIterator.hasNext())
                        {
                            String keyStr = (String)keysIterator.next();

                            m = new SubscriptionPlanModel();
                            m.setPlan_name(keyStr.toUpperCase());
                            m.setPlan_price(basic.getJSONObject(keyStr).getJSONObject("year").getInt("amount"));
                            m.setPlan_price_monthly(basic.getJSONObject(keyStr).getJSONObject("month").getInt("amount"));
                            if(plans.size()==0)
                                m.setPlan_status(true);
                            else
                                m.setPlan_status(false);
                            plans.add(m);



                        }

                        try {
                            JSONObject extra_plan = responseObject.getJSONObject("extra_plan");
                            JSONObject recovery = extra_plan.getJSONObject("recovery");
                            recovery_amount = recovery.getDouble("amount");
                            String desc = recovery.getString("description");
                            recoverydescription.setText(desc);
                            isRecoveryAvailable = true;




                        } catch (JSONException e) {
                            e.printStackTrace();
                            isRecoveryAvailable = false;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                    selected_model = plans.get(0);
                    if(!selected_model.getPlan_name().equalsIgnoreCase("PREMIUM") && isRecoveryAvailable)
                    {
                        recovery_view.setVisibility(View.VISIBLE);
                        recoveryPlan.setChecked(true);

                    }
                    else
                    {
                        recovery_view.setVisibility(View.GONE);
                        recoveryPlan.setChecked(false);
                    }
                    adapter = new CustomBaseAdapter(SubscriptionPlanSelection.this);
                    plan_list.setAdapter(adapter);
                    continue_button.setEnabled(true);


                }

                else
                {
                    Utility.showToast(this, "Plans not available !!");
                    continue_button.setEnabled(false);
                }



            } catch (JSONException e) {
                e.printStackTrace();
                Utility.showToast(this, "Plans not available !!");
                continue_button.setEnabled(false);
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
        continue_button.setEnabled(false);

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


    private class CustomBaseAdapter extends BaseAdapter {
        Context context;

        private CustomBaseAdapter(Context context) {
            this.context = context;
        }

        /*private view holder class*/
        private class ViewHolder {
            ImageView planStatus;
            TextView plan_name;
            TextView plan_rate;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.subscription_planlistitem, null);
                holder = new ViewHolder();
                holder.plan_name = (TextView) convertView.findViewById(R.id.plan_name);
                holder.plan_rate = (TextView) convertView.findViewById(R.id.plan_rate);
                holder.planStatus = (ImageView) convertView.findViewById(R.id.planStatus);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            SubscriptionPlanModel rowItem = (SubscriptionPlanModel) getItem(position);

            holder.plan_name.setText(rowItem.getPlan_name());

            if (!rowItem.getPlan_status())
                holder.planStatus.setImageResource(R.drawable.selectedradio);
            else
                holder.planStatus.setImageResource(R.drawable.unselectedradio);
            holder.plan_rate.setText("₦" + rowItem.getPlan_price_monthly() + " per Month, ₦" + rowItem.getPlan_price() + " per Year");



            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(!rowItem.getPlan_name().equalsIgnoreCase("PREMIUM") && isRecoveryAvailable)
                    {
                        recovery_view.setVisibility(View.VISIBLE);
                        recoveryPlan.setChecked(true);

                    }
                    else {

                        recovery_view.setVisibility(View.GONE);
                        recoveryPlan.setChecked(false);
                    }

                    for (int i = 0; i < plans.size(); i++) {
                        if (plans.get(i).getPlan_name().equalsIgnoreCase(rowItem.getPlan_name())) {
                            SubscriptionPlanModel model = plans.get(i);
                            model.setPlan_status(true);
                            plans.set(i, model);
                        } else {
                            SubscriptionPlanModel model = plans.get(i);
                            model.setPlan_status(false);
                            plans.set(i, model);
                        }
                    }


                    notifyDataSetChanged();

                    selected_model = rowItem;
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return plans.size();
        }

        @Override
        public Object getItem(int position) {
            return plans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return plans.indexOf(getItem(position));
        }
    }

    private void getSubscriptionPlans() {
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
        String url = ApiConstants.BASE_URL+"payment/plan/"+userId;
        // String url = ApiConstants.urlBuilderCotactsWithTwoPara(ApiConstants.API_ADDRESS.DIAGNOSE.path, null, userId, Utility.getImei(this));
        ApiCall.getInstance().makeGetRequestWithUrl(SubscriptionPlanSelection.this, url, null, header, this, 211);

    }


}
