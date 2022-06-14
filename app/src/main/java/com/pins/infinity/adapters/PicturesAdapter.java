package com.pins.infinity.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pins.infinity.R;
import com.pins.infinity.activity.FullImageActivity;
import com.pins.infinity.model.PictureBaseModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bimalchawla on 16/3/17.
 */

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.MyViewHolder> {

    Context context;
    private List<PictureBaseModel> pictureList;

    public PicturesAdapter(Context con, List<PictureBaseModel> list) {
        this.pictureList = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_picture, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PictureBaseModel model = pictureList.get(position);
        holder.time.setText(model.getDate());
        holder.time.setTag(model.getFileName());
        holder.title.setText(model.getImageUrl());
        if(model.getImageUrl()!=null) {
            System.out.println("url is   "+model.getImageUrl());
            Picasso.with(context)
                    .load(model.getImageUrl())
                    .resize(100, 100)
                    .placeholder(R.drawable.placeholder) // optional
                    .error(R.drawable.placeholder)         // optional
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView time, title;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.time_picture);
            title = (TextView) view.findViewById(R.id.title_picture);
            image = (ImageView) view.findViewById(R.id.picture_thumb);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent fullImage = new Intent(context, FullImageActivity.class);
                    fullImage.putExtra("fileName", time.getTag().toString());
                    fullImage.putExtra("imageUrl", title.getText());

                    context.startActivity(fullImage);
                }
            });
        }
    }

//    public void remove(int position) {
//        PictureBaseModel model = pictureList.get(position);
//        if (pictureList.contains(model)) {
//            pictureList.remove(position);
//            notifyItemRemoved(position);
//        }
//    }
}
