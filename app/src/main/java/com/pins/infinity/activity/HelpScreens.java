package com.pins.infinity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pins.infinity.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shri.kant on 26-10-2016.
 */
public class HelpScreens extends AppCompatActivity {
    private ViewPager viewpager;
    ViewPagerAdapter adapter;
    ArrayList<Integer> images = new ArrayList<>();
    RelativeLayout sliderfooter;
    ImageView one, two, three,four,five,six,seven,eight;
    TextView skip,done;
    final Handler handler = new Handler();
    Timer timer;
    final long DELAY_MS = 3000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.helpscreens);

        sliderfooter = (RelativeLayout) findViewById(R.id.sliderfooter);
        one = (ImageView)findViewById(R.id.one);
        two = (ImageView)findViewById(R.id.two);
        three = (ImageView)findViewById(R.id.three);
        four = (ImageView)findViewById(R.id.four);
        five = (ImageView)findViewById(R.id.five);
        six = (ImageView) findViewById(R.id.six);
        seven = (ImageView)findViewById(R.id.seven);
        eight = (ImageView) findViewById(R.id.eight);
        skip = (TextView)findViewById(R.id.skip);
        done = (TextView)findViewById(R.id.done);

        done.setVisibility(View.GONE);
        eight.setVisibility(View.GONE);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpScreens.this, LoginRegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpScreens.this, LoginRegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });


        try {
            images.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        images.add(R.drawable.help1);
        images.add(R.drawable.help2);
       //images.add(R.drawable.help3);
        images.add(R.drawable.help4);
        images.add(R.drawable.help5);
        images.add(R.drawable.help6);
        images.add(R.drawable.help7);
        images.add(R.drawable.help8);

        viewpager = (ViewPager)findViewById(R.id.viewpager);

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
                        skip.setVisibility(View.VISIBLE);
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
                        skip.setVisibility(View.VISIBLE);
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
                        skip.setVisibility(View.VISIBLE);
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
                        skip.setVisibility(View.VISIBLE);
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
                        skip.setVisibility(View.VISIBLE);
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
                        skip.setVisibility(View.VISIBLE);
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
                        skip.setVisibility(View.GONE);
                        done.setVisibility(View.VISIBLE);
                        break;


                 /*   case 7:
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
                        skip.setVisibility(View.GONE);
                        done.setVisibility(View.VISIBLE);
                        break;
*/




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
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(HelpScreens.this, LoginRegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();

    }
}
