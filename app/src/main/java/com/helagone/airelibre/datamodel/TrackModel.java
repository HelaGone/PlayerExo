package com.helagone.airelibre.datamodel;

import java.io.Serializable;

/**
 * Created by HELA on 2/25/18.
 */

public class TrackModel implements Serializable {
    private String trk_title, trk_artist, trk_duration;

    public TrackModel(String title, String artist, String duration){
        this.trk_title = title;
        this.trk_artist = artist;
        this.trk_duration = duration;
    }

    public String getTrk_title(){
        return trk_title;
    }
    public void setTrk_title(String title){
        this.trk_artist = title;
    }

    public String getTrk_duration(){
        return trk_duration;
    }
    public void setTrk_duration(String album){
        this.trk_duration = album;
    }

    public String getTrk_artist(){
        return trk_artist;
    }
    public void setTrk_artist(String artist){this.trk_artist = artist;}
}
