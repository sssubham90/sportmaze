package com.devil.sportmaze;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> implements Filterable{
    private Context mcontext;
    private StorageReference storageReference;
    private String generatedFilePath;
    private ArrayList<Video> mArrayList;
    private ArrayList<Video> mFilteredList;
    private ProgressDialog dialog;

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;
        MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            name = view.findViewById(R.id.name);
        }
    }

    public ListAdapter(Context context, ArrayList<Video> videoList) {
        this.mFilteredList = videoList;
        this.mArrayList = videoList;
        mcontext = context;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Video videoElement = mFilteredList.get(position);
        holder.name.setText(videoElement.getName());
        GlideApp.with(mcontext)
                .load(storageReference.child("Images").child(videoElement.getKey()).child("thumbnail.png"))
                .into(holder.thumbnail);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(mcontext);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Loading. Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                storageReference.child("Videos").child(videoElement.getKey()).child("video.mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        generatedFilePath = uri.toString();
                        dialog.dismiss();
                        mcontext.startActivity(new Intent(mcontext, VideoPlayerActivity.class).putExtra("name",videoElement.getName()).putExtra("url", generatedFilePath).putExtra("key",videoElement.getKey()));
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
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mArrayList;
                } else {
                    ArrayList<Video> filteredList = new ArrayList<>();
                    for (Video video : mArrayList) {
                        if (video.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(video);
                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Video>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}