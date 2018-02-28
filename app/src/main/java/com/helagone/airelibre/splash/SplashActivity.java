package com.helagone.airelibre.splash;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.VideoView;
import com.helagone.airelibre.MainActivity;
import com.helagone.airelibre.R;

public class SplashActivity extends AppCompatActivity {

    private void jump(){

        if(isFinishing()){
            return;
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }//END JUMP METHOD

    @Override
    public boolean onTouchEvent(MotionEvent event){
        jump();
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try{
            VideoView videoHolder = findViewById(R.id.splashVideoView);
            Uri videoUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.al_logo_animation_v3);
            videoHolder.setVideoURI(videoUri);

            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                @Override
                public void onCompletion(MediaPlayer mp){
                    jump();
                }
            });
            videoHolder.start();
        }catch(Exception ex){
            jump();
        }
    }

}
