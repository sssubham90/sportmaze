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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    String key,name;
    FirebaseUser user;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();
        String url;
        user = FirebaseAuth.getInstance().getCurrentUser();
        url = getIntent().getStringExtra("url");
        name = getIntent().getStringExtra("name");
        key = getIntent().getStringExtra("key");
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
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(c);
        String msg = getTime(videoView.getCurrentPosition())+"/"+getTime(videoView.getDuration());
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        if(user.getEmail()!=null){
            myRef.child("Users").child(user.getDisplayName()+"("+user.getEmail().substring(0,user.getEmail().indexOf('@'))+")").child(date).child(name).setValue(msg);
            myRef.child("Video").child(user.getDisplayName()).child(date).child(name).setValue(msg);
        }
        finish();
    }
}
