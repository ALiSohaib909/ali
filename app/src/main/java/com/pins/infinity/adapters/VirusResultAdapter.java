package com.pins.infinity.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pins.infinity.R;

import java.util.List;

/**
 * Created by bimalchawla on 21/1/17.
 */

public class VirusResultAdapter extends RecyclerView.Adapter<VirusResultAdapter.MyViewHolder> {

    private List virusList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.virus_name);
        }
    }


    public VirusResultAdapter(List list) {
        this.virusList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_virus_scan, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String st = (String) virusList.get(position);
        holder.name.setText(st);
    }

    @Override
    public int getItemCount() {
        return virusList.size();
    }
}
