package com.devil.sportmaze;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SliderAdapter extends PagerAdapter {

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private LayoutInflater inflater;
    private Context context;
    private int value;
    private String generatedFilePath;
    private String name;
    private String key;
    private ImageView myImage;

    public SliderAdapter(Context context, int value) {
        this.context = context;
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Featured Videos");
        inflater = LayoutInflater.from(context);
        this.value = value;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return value;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, final int position) {
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        myImage = myImageLayout.findViewById(R.id.image);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("t0ttt",String.valueOf(position));
                key = dataSnapshot.child(String.valueOf(position)).getValue().toString();
                GlideApp.with(context)
                        .load(storageReference.child("Images").child(key).child("thumbnail.png"))
                        .into(myImage);
                myImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        storageReference.child("Videos").child(key).child("video.mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Video");
                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        name = dataSnapshot.child(key).child("Name").getValue().toString();
                                        generatedFilePath = uri.toString(); /// The string(file link) that you need
                                        context.startActivity(new Intent(context, VideoPlayerActivity.class).putExtra("url", generatedFilePath).putExtra("name",name).putExtra("key",key));
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Log.w("sm", "Failed to read value.", error.toException());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        view.addView(myImageLayout,position);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
