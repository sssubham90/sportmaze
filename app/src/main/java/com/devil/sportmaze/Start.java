package com.devil.sportmaze;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class Start extends Fragment {

    private ViewPager mPager;
    private int currentPage=0,value = 2;
    private SliderAdapter mAdapter;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);
        rootView.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).goToGallery();
            }
        });
        CircleIndicator indicator = rootView.findViewById(R.id.indicator);
        mPager = rootView.findViewById(R.id.pager);
        mAdapter = new SliderAdapter(getActivity(),value);
        mPager.setAdapter(mAdapter);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == value) {
                    mPager.setCurrentItem(currentPage=0, false);
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
