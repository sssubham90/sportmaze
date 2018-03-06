package com.devil.sportmaze;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class Start extends Fragment {

    private ViewPager mPager;
    private int currentPage=0,value;
    private SliderAdapter mAdapter;
    private View rootView;
    private StorageReference storageReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_start, container, false);
        rootView.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).goToGallery();
            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();
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
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Featured Video");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GlideApp.with(getActivity())
                        .load(storageReference.child("Images").child(dataSnapshot.child("0").getValue()!=null?dataSnapshot.child("0").getValue().toString():"").child("thumbnail.png"))
                        .into((ImageView) rootView.findViewById(R.id.image1));
                GlideApp.with(getActivity())
                        .load(storageReference.child("Images").child(dataSnapshot.child("1").getValue()!=null?dataSnapshot.child("1").getValue().toString():"").child("thumbnail.png"))
                        .into((ImageView) rootView.findViewById(R.id.image2));
                GlideApp.with(getActivity())
                        .load(storageReference.child("Images").child(dataSnapshot.child("2").getValue()!=null?dataSnapshot.child("2").getValue().toString():"").child("thumbnail.png"))
                        .into((ImageView) rootView.findViewById(R.id.image3));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return rootView;
    }

}
