package com.pins.infinity.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pins.infinity.R;
import com.pins.infinity.model.ContactsModel;

import java.util.List;

/**
 * Created by shri.kant on 10-11-2016.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private List<ContactsModel> constactList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cont_name, year, cont_no;

        public MyViewHolder(View view) {
            super(view);
            cont_name = (TextView) view.findViewById(R.id.cont_name);
            cont_no = (TextView) view.findViewById(R.id.cont_no);
//            year = (TextView) view.findViewById(R.id.year);
        }
    }


    public ContactsAdapter(List<ContactsModel> constactList) {
        this.constactList = constactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ContactsModel ContactsModel = constactList.get(position);
        holder.cont_name.setText(ContactsModel.getName());
        holder.cont_no.setText(ContactsModel.getMobileNo());
//        holder.year.setText(ContactsModel.getYear());
    }

    @Override
    public int getItemCount() {
        return constactList.size();
    }
}
