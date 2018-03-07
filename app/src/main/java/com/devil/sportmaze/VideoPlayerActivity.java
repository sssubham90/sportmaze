package com.devil.sportmaze;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    String key;
    FirebaseUser user;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();
        String url,name;
        user = FirebaseAuth.getInstance().getCurrentUser();
        url = getIntent().getStringExtra("url");
        name = getIntent().getStringExtra("name");
        key = getIntent().getStringExtra("key");
        ((TextView)findViewById(R.id.head)).setText(name);
        videoView = findViewById(R.id.myVideo);
        videoView.setVideoURI(Uri.parse(url));
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                dialog.dismiss();
                videoView.start();
            }
        });
    }

    String getTime(int s){
        int m = s/600000;
        s = (s-(m*60000))/1000;
        return String.format("%dm:%ds",m,s);
    }

    @Override
    public void onBackPressed() {
        String msg = getTime(videoView.getCurrentPosition())+"/"+getTime(videoView.getDuration());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Video");
        if(user.getDisplayName()!=null)
            myRef.child(key).child("Viewers").child(user.getDisplayName()).setValue(msg);
        finish();
    }
}
