package com.pins.infinity.adapters;

/**
 * Created by bimalchawla on 16/2/17.
 */

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pins.infinity.R;
import com.pins.infinity.activity.VirusScanActivity;
import com.pins.infinity.model.ScanModel;
import com.pins.infinity.utility.AppConstants;

import java.util.List;


public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.MyViewHolder> {

    private List<ScanModel> scanList;
    Context con;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.scan_date_text);

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(con, VirusScanActivity.class);
                        AppConstants.scanModel = (ScanModel) name.getTag();
                        con.startActivity(intent);
                }
            });
        }
    }


    public ScanHistoryAdapter(Context con, List<ScanModel> list) {
        this.con = con;
        this.scanList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scan_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ScanModel st = scanList.get(position);
        holder.name.setText(st.getDate());
        holder.name.setTag(st);
    }

    @Override
    public int getItemCount() {
        return scanList.size();
    }
}
