package com.helagone.airelibre.datafetch;
import android.net.Uri;

/**
 * Created by HELA on 3/1/18.
 */

public class CoverFetcher {
    public String FetchCover(String endpoint){
        String coverUrl = null;
        endpoint = endpoint+"?";
        try{
            coverUrl = Uri.parse(endpoint).buildUpon().build().toString();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        if(coverUrl != null) return coverUrl;
        return null;
    }
}
