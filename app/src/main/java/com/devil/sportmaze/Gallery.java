package com.devil.sportmaze;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends Fragment {
    List<Video> videoList;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        videoList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new ListAdapter(getActivity(), videoList));
        AddImagesUrlOnline();
        SearchView mSearchView = rootView.findViewById(R.id.search_bar);
        mSearchView.setVisibility(View.GONE);
        return rootView;
    }

    public void AddImagesUrlOnline(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Video");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                    videoList.add(new Video(childDataSnapshot.child("Name").getValue().toString(), childDataSnapshot.child("URL").getValue().toString(), childDataSnapshot.child("Image URL").getValue().toString()));
                    recyclerView.getAdapter().notifyItemInserted(videoList.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("sm", "Failed to read value.", error.toException());
            }
        });
    }
}
