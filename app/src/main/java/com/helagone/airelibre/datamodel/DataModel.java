package com.helagone.airelibre.datamodel;

/**
 * Created by HELA on 2/25/18.
 */

public class DataModel {
    private String artistName;
    private String trackName;
    private String albumName;
    private String duration;
    private String startTime;

    public String getArtistName(){return artistName;}
    public void setArtistName(String aName){
        this.artistName = aName;
    }

    public String getTrackName(){return trackName;}
    public void setTrackName(String tName){
        this.trackName = tName;
    }

    public String getAlbumName(){return albumName;}
    public void setAlbumName(String aName){
        this. albumName = aName;
    }


    public String getDuration(){
        return duration;
    }
    public void setDuration(String dur){
        this.duration = dur;
    }

    public String getStartTime(){return startTime;}
    public void setStartTime(String sTime){
        this.startTime = sTime;
    }
}
