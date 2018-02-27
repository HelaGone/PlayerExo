package com.helagone.airelibre.datamodel;

/**
 * Created by HELA on 2/27/18.
 */

public class FavsDataModel {
    private String trackArtistname;
    private String trackTitle;
    private String trackDuration;

    public String getTrackArtistname(){
        return trackArtistname;
    }
    public void setTrackArtistname(String tArtistname) {
        trackArtistname = tArtistname;
    }


    public String getTrackTitle(){
        return trackTitle;
    }
    public void setTrackTitle(String tTitle){
        trackTitle = tTitle;
    }


    public String getTrackDuration(){
        return trackDuration;
    }
    public void setTrackDuration(String tDuration){
        trackDuration = tDuration;
    }
}
