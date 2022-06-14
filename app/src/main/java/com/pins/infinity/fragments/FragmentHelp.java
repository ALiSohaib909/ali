package com.pins.infinity.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pins.infinity.R;
import com.pins.infinity.activity.MainActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shri.kant on 08-11-2016.
 */
public class FragmentHelp extends Fragment {
    final Handler handler = new Handler();
    final long DELAY_MS = 3000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    ViewPagerAdapter adapter;
    ArrayList<Integer> images = new ArrayList<>();
    RelativeLayout sliderfooter;
    ImageView one, two, three,four,five,six,seven,eight;
    TextView skip,done;
    Timer timer;
    private View view = null;
    private ViewPager viewpager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.helpscreens,
                container, false);

        sliderfooter = (RelativeLayout) view.findViewById(R.id.sliderfooter);
        one = (ImageView) view.findViewById(R.id.one);
        two = (ImageView) view.findViewById(R.id.two);
        three = (ImageView) view.findViewById(R.id.three);
        four = (ImageView) view.findViewById(R.id.four);
        five = (ImageView) view.findViewById(R.id.five);
        six = (ImageView)  view.findViewById(R.id.six);
        seven = (ImageView) view.findViewById(R.id.seven);
        eight = (ImageView)  view.findViewById(R.id.eight);
        skip = (TextView) view.findViewById(R.id.skip);
        done = (TextView) view.findViewById(R.id.done);

        skip.setVisibility(View.GONE);
        done.setVisibility(View.GONE);
        eight.setVisibility(View.GONE);



        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    MainActivity.navigationView.setCheckedItem(R.id.home);
                    MainActivity.updateTitle("Home");
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentHome()).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        try {
            images.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        images.add(R.drawable.help1);
        images.add(R.drawable.help2);
       // images.add(R.drawable.help3);
        images.add(R.drawable.help4);
        images.add(R.drawable.help5);
        images.add(R.drawable.help6);
        images.add(R.drawable.help7);
        images.add(R.drawable.help8);

        viewpager = (ViewPager) view.findViewById(R.id.viewpager);

        adapter = new ViewPagerAdapter(images);
        viewpager.setAdapter(adapter);

        /*After setting the adapter use the timer */

        final Runnable Update = new Runnable() {
            public void run() {
                viewpager.setCurrentItem(viewpager.getCurrentItem()+1);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                if (timer != null)
                    timer.cancel(); // This will create a new Thread

                timer = new Timer(); // This will create a new Thread
                timer .schedule(new TimerTask() { // task to be scheduled

                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, DELAY_MS, PERIOD_MS);

                switch (position) {
                    case 0:
                        one.setImageResource(R.drawable.active);
                        two.setImageResource(R.drawable.inactive);
                        three.setImageResource(R.drawable.inactive);
                        four.setImageResource(R.drawable.inactive);
                        five.setImageResource(R.drawable.inactive);
                        six.setImageResource(R.drawable.inactive);
                        seven.setImageResource(R.drawable.inactive);
                        eight.setImageResource(R.drawable.inactive);
                        sliderfooter.setBackgroundResource(R.color.help1);
                        viewpager.setBackgroundResource(R.color.help1);
                        done.setVisibility(View.GONE);
                        break;

                    case 1:
                        one.setImageResource(R.drawable.active);
                        two.setImageResource(R.drawable.active);
                        three.setImageResource(R.drawable.inactive);
                        four.setImageResource(R.drawable.inactive);
                        five.setImageResource(R.drawable.inactive);
                        six.setImageResource(R.drawable.inactive);
                        seven.setImageResource(R.drawable.inactive);
                        eight.setImageResource(R.drawable.inactive);
                        sliderfooter.setBackgroundResource(R.color.help2);
                        viewpager.setBackgroundResource(R.color.help2);
                        done.setVisibility(View.GONE);
                        break;

                    case 2:
                        one.setImageResource(R.drawable.active);
                        two.setImageResource(R.drawable.active);
                        three.setImageResource(R.drawable.active);
                        four.setImageResource(R.drawable.inactive);
                        five.setImageResource(R.drawable.inactive);
                        six.setImageResource(R.drawable.inactive);
                        seven.setImageResource(R.drawable.inactive);
                        eight.setImageResource(R.drawable.inactive);
                        sliderfooter.setBackgroundResource(R.color.help4);
                        viewpager.setBackgroundResource(R.color.help4);
                        done.setVisibility(View.GONE);
                        break;

                    case 3:
                        one.setImageResource(R.drawable.active);
                        two.setImageResource(R.drawable.active);
                        three.setImageResource(R.drawable.active);
                        four.setImageResource(R.drawable.active);
                        five.setImageResource(R.drawable.inactive);
                        six.setImageResource(R.drawable.inactive);
                        seven.setImageResource(R.drawable.inactive);
                        eight.setImageResource(R.drawable.inactive);
                        sliderfooter.setBackgroundResource(R.color.help5);
                        viewpager.setBackgroundResource(R.color.help5);
                        done.setVisibility(View.GONE);
                        break;

                    case 4:
                        one.setImageResource(R.drawable.active);
                        two.setImageResource(R.drawable.active);
                        three.setImageResource(R.drawable.active);
                        four.setImageResource(R.drawable.active);
                        five.setImageResource(R.drawable.active);
                        six.setImageResource(R.drawable.inactive);
                        seven.setImageResource(R.drawable.inactive);
                        eight.setImageResource(R.drawable.inactive);
                        sliderfooter.setBackgroundResource(R.color.help6);
                        viewpager.setBackgroundResource(R.color.help6);
                        done.setVisibility(View.GONE);
                        break;

                    case 5:
                        one.setImageResource(R.drawable.active);
                        two.setImageResource(R.drawable.active);
                        three.setImageResource(R.drawable.active);
                        four.setImageResource(R.drawable.active);
                        five.setImageResource(R.drawable.active);
                        six.setImageResource(R.drawable.active);
                        seven.setImageResource(R.drawable.inactive);
                        eight.setImageResource(R.drawable.inactive);
                        sliderfooter.setBackgroundResource(R.color.help7);
                        viewpager.setBackgroundResource(R.color.help7);
                        done.setVisibility(View.GONE);
                        break;

                    case 6:
                        one.setImageResource(R.drawable.active);
                        two.setImageResource(R.drawable.active);
                        three.setImageResource(R.drawable.active);
                        four.setImageResource(R.drawable.active);
                        five.setImageResource(R.drawable.active);
                        six.setImageResource(R.drawable.active);
                        seven.setImageResource(R.drawable.active);
                        eight.setImageResource(R.drawable.inactive);
                        sliderfooter.setBackgroundResource(R.color.help8);
                        viewpager.setBackgroundResource(R.color.help8);
                        done.setVisibility(View.VISIBLE);
                        break;


                  /*  case 7:
                        one.setImageResource(R.drawable.active);
                        two.setImageResource(R.drawable.active);
                        three.setImageResource(R.drawable.active);
                        four.setImageResource(R.drawable.active);
                        five.setImageResource(R.drawable.active);
                        six.setImageResource(R.drawable.active);
                        seven.setImageResource(R.drawable.active);
                        eight.setImageResource(R.drawable.active);
                        sliderfooter.setBackgroundResource(R.color.help8);
                        viewpager.setBackgroundResource(R.color.help8);
                        done.setVisibility(View.VISIBLE);
                        break;*/





                    default:
                        break;
                }


            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private class ViewPagerAdapter extends PagerAdapter {

        ArrayList<Integer> images = new ArrayList<>();

        public ViewPagerAdapter(ArrayList<Integer> images) {

            super();
            this.images = images;

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
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Setting view you want to display as a row element
            View view = inflater.inflate(R.layout.viewpager, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
            imageView.setImageResource(images.get(position));
            container.addView(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(viewpager.getCurrentItem()<7)
                    {

                        viewpager.setCurrentItem(viewpager.getCurrentItem()+1);

                    }


                }
            });

            return view;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }


}