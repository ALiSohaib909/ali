package com.pins.infinity.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pins.infinity.R;

/**
 * Created by abc on 9/19/2017.
 */

public class PaymentSelection extends AppCompatActivity {
    private ImageView back;
    private Button proceed;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    int amount;
    String plan_name;
    private String trans_id = "";
    int total_in_unit = 0;

    public static final String TRANSACTION_ID = "trans_id";
    public static final String TOTAL_AMOUNT = "total_amount";
    public static final String PLAN_NAME = "plan_name";
    public static final String TOTAL_IN_UNIT = "total_in_unit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.payment_selection);


        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        trans_id = bundle.getString(TRANSACTION_ID);
        amount = (int)bundle.get(TOTAL_AMOUNT);
        plan_name = bundle.getString(PLAN_NAME);
        total_in_unit = bundle.getInt(TOTAL_IN_UNIT);

        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);

        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        proceed = (Button) findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId=radioGroup.getCheckedRadioButtonId();
                radioButton=(RadioButton)findViewById(selectedId);

                if(selectedId == R.id.card)
                {
                    Intent intent = new Intent(PaymentSelection.this,PayActivity.class);
                    intent.putExtra(TRANSACTION_ID,trans_id);
                    intent.putExtra(TOTAL_AMOUNT,amount);
                    intent.putExtra(PLAN_NAME,plan_name);
                    intent.putExtra(TOTAL_IN_UNIT,total_in_unit);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }



            }
        });
    }
}
