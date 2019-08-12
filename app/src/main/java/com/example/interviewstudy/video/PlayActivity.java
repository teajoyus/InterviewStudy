package com.example.interviewstudy.video;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.example.interviewstudy.R;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play);
        VideoView video = findViewById(R.id.video);
        video.setVideoPath("https://m3u8.44cdn.com/video-hls/89/2019/03-1/nxSq5hPj/hls/nxSq5hPj.m3u8");
        video.start();
    }
    private void setOrientation(){
        if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
