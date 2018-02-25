package com.helagone.airelibre.datamodel;

import java.io.Serializable;

/**
 * Created by HELA on 2/25/18.
 */

public class TrackModel implements Serializable {
    private String trk_title, trk_album, trk_artist, trk_duration;

    public TrackModel(String title, String album, String artist){
        this.trk_title = title;
        this.trk_album = album;
        this.trk_artist = artist;
        //this.trk_duration = duration;
    }

    public String getTrk_title(){
        return trk_title;
    }
    public void setTrk_title(String title){
        this.trk_artist = title;
    }

    public String getTrk_album(){
        return trk_album;
    }
    public void setTrk_album(String album){
        this.trk_album = album;
    }

    public String getTrk_artist(){
        return trk_artist;
    }
    public void setTrk_artist(String artist){this.trk_artist = artist;}
}
