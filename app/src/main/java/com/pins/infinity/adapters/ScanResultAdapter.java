package com.pins.infinity.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pins.infinity.R;
import com.pins.infinity.model.ScanResultModel;

import java.util.List;

/**
 * Created by bimalchawla on 12/2/17.
 */

public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.MyViewHolder> {

    private List<ScanResultModel> scanList;
    List virusList;
    Context con;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, number;
        ImageView logo, arrow;
        RelativeLayout mainLayout;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.scan_app_name);
            number = (TextView) view.findViewById(R.id.virus_number);
            logo = (ImageView) view.findViewById(R.id.scan_result_logo);
            arrow = (ImageView) view.findViewById(R.id.scan_result_arrow);
            mainLayout = (RelativeLayout) view.findViewById(R.id.item_scan_result_layout);


        }
    }


    public ScanResultAdapter(Context con, List<ScanResultModel> list, List<String> virus) {
        this.con = con;
        this.scanList = list;
        this.virusList= virus;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scan_result, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ScanResultModel st = scanList.get(position);
        holder.name.setText(st.getName());
        holder.number.setText(st.getNumber());
        if(st.getName().equalsIgnoreCase(con.getString(R.string.total_scan))) {
            holder.logo.setVisibility(View.GONE);
            holder.number.setTextSize(20);
            holder.name.setTextSize(20);
        } else {
            holder.logo.setImageResource(st.getImage());

        }
        if(st.getName().equalsIgnoreCase(con.getString(R.string.bad_scan)))
        {
            holder.arrow.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.arrow.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return scanList.size();
    }
}
