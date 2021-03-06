package com.helagone.airelibre.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by programacion2 on 2/23/18.
 */

public class RadioManager {

    private static RadioManager instance = null;
    private static RadioService service;
    private Context context;
    private boolean serviceBound;

    public RadioManager(Context context) {
        this.context = context;
        serviceBound = false;
    }

    public static RadioManager with(Context context) {

        if (instance == null)
            instance = new RadioManager(context);

        return instance;
    }

    public static RadioService getService(){
        return service;
    }

    public void playOrPause(String streamUrl){
        service.playOrPause(streamUrl);
    }

    public boolean isPlaying() {

        if(service != null){
            return service.isPlaying();
        }
        return false;
    }

    public int getCurrentPosition(){

        if(service != null){
            return service.getCurrentPosition();
        }

        return 0;
    }

    public void bind() {

        Intent intent = new Intent(context, RadioService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        if(service != null)
            EventBus.getDefault().post(service.getStatus());
    }

    public void unbind() {

        context.unbindService(serviceConnection);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {

            service = ((RadioService.LocalBinder) binder).getService();
            serviceBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            serviceBound = false;
        }
    };



}
