package com.helagone.airelibre;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.helagone.airelibre.service.PlaybackStatus;
import com.helagone.airelibre.service.RadioManager;
import com.helagone.airelibre.utility.Shoutcast;
import com.helagone.airelibre.utility.ShoutcastHelper;
import com.helagone.airelibre.utility.ShoutcastListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    RadioManager radioManager;

    ImageButton trigger;
    TextView textView;

    String streamURL;

    private List<Shoutcast> shoutcasts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shoutcasts = ShoutcastHelper.retrieveShoutcasts(this);

        Log.d("shoutcasts >>>>>>>>>> ", String.valueOf(shoutcasts.get(0).getName()));

        trigger = findViewById(R.id.id_trigger);
        textView = findViewById(R.id.lbl_trackinfo);

        radioManager = new RadioManager(this);

        trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(shoutcasts.get(0).getName());
                streamURL = shoutcasts.get(0).getUrl();
                if(TextUtils.isEmpty(streamURL)) return;
                radioManager.playOrPause(streamURL);
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public void onStop(){
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    protected void onDestroy(){
        super.onDestroy();
        radioManager.unbind();
    }

    protected void onResume(){
        super.onResume();
        radioManager.bind();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Subscribe
    public void onEvent(String status){

        switch (status){

            case PlaybackStatus.LOADING:

                // loading

                break;

            case PlaybackStatus.ERROR:

                Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show();

                break;

        }

        trigger.setImageResource(status.equals(PlaybackStatus.PLAYING)
                ? R.drawable.ic_pause_black
                : R.drawable.ic_play_arrow_black);
    }






}
