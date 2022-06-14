package com.pins.infinity.adapters;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pins.infinity.R;

import java.util.ArrayList;

/**
 * Created by abc on 9/17/2017.
 */

public class ViewPagerAdapter  extends PagerAdapter {

    ArrayList<Integer> images = new ArrayList<>();
    Context context;

    public ViewPagerAdapter(ArrayList<Integer> images, Context context) {

        super();
        this.images = images;
        this.context = context;

    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View collection, Object object) {
        return collection == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)  {

        // Inflating layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Setting view you want to display as a row element
        View view = inflater.inflate(R.layout.viewpager, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(images.get(position));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}



