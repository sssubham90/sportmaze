package com.devil.sportmaze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class Start extends Fragment implements RewardedVideoAdListener{

    private ViewPager mPager;
    private int currentPage=0,value;
    private SliderAdapter mAdapter;
    private View rootView;
    private StorageReference storageReference;
    private CircleIndicator indicator;
    private String generatedFilePath;
    private ProgressDialog dialog;
    private ImageView[] imageViews;
    private TextView[] textViews;
    RewardedVideoAd mAd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_start, container, false);
        MobileAds.initialize(getActivity(),getString(R.string.reward1));
        mAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mAd.setRewardedVideoAdListener(this);
        mAd.loadAd(getString(R.string.reward1),new AdRequest.Builder().build());
        ImageView mImage = (ImageView)rootView.findViewById(R.id.image4);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAd.isLoaded()){
                    mAd.show();
                }
            }
        });

        rootView.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).goToGallery();
            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Featured Videos");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value = (int) dataSnapshot.getChildrenCount();
                indicator = rootView.findViewById(R.id.indicator);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        textViews = new TextView[3];
        imageViews = new ImageView[3];
        imageViews[0] = rootView.findViewById(R.id.image1);
        textViews[0] = rootView.findViewById(R.id.text1);
        imageViews[1] = rootView.findViewById(R.id.image2);
        textViews[1] = rootView.findViewById(R.id.text2);
        imageViews[2] = rootView.findViewById(R.id.image3);
        textViews[2] = rootView.findViewById(R.id.text3);
        FirebaseDatabase.getInstance().getReference("Video").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(final DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                    if(i==3) break;
                    final String key = childSnapshot.getKey();
                    GlideApp.with(getActivity())
                            .load(storageReference.child("Images").child(key).child("thumbnail.png"))
                            .into(imageViews[i]);
                    textViews[i].setText(childSnapshot.child("Name").getValue().toString());
                    imageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog = new ProgressDialog(getActivity());
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage("Loading. Please wait...");
                            dialog.setIndeterminate(true);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            storageReference.child("Videos").child(key).child("video.mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    generatedFilePath = uri.toString();
                                    dialog.dismiss();
                                    getActivity().startActivity(new Intent(getActivity(), VideoPlayerActivity.class).putExtra("name", "").putExtra("url", generatedFilePath).putExtra("key", key).putExtra("name",childSnapshot.child("Name").getValue().toString()));
                                }
                            });
                        }
                    });
                    i++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }
}