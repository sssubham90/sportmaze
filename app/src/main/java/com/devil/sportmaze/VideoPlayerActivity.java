package com.devil.sportmaze;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    String key,name;
    FirebaseUser user;
    private ProgressDialog dialog;
    private long dur;
    private DatabaseReference myRef;

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
                videoView.seekTo(100);
                videoView.start();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
    }

    String getTime(int s){
        int m = s/60000;
        s = (s-(m*60000))/1000;
        return String.format("%dm:%02ds",m,s);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK  && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(c);
        final String msg = getTime((videoView.getCurrentPosition()!=0)?videoView.getCurrentPosition():videoView.getDuration())+"/"+getTime(videoView.getDuration());
        myRef = FirebaseDatabase.getInstance().getReference();
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        if(user.getEmail()!=null){
            myRef.child("Users").child(user.getDisplayName()+"("+user.getEmail().substring(0,user.getEmail().indexOf('@'))+")").child(date).child(name).setValue(msg);
            myRef.child("Users").child(user.getDisplayName()+"("+user.getEmail().substring(0,user.getEmail().indexOf('@'))+")").child("Total Duration").addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       dur=dataSnapshot.getValue()!=null?Long.parseLong(dataSnapshot.getValue().toString()):0;
                       myRef.child("Users").child(user.getDisplayName()+"("+user.getEmail().substring(0,user.getEmail().indexOf('@'))+")").child("Total Duration").setValue(dur+(videoView.getCurrentPosition()/60000));
                   }
                   @Override
                   public void onCancelled(DatabaseError databaseError) {
                   }
            });
            myRef.child("Video").child(key).child("Viewers").child(user.getDisplayName()).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
        else {
            dialog.dismiss();
            finish();
        }
    }
}
