package com.helagone.airelibre.datafetch;

import android.net.Uri;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HELA on 2/25/18.
 */

public class CurrentMetadataFetcher {
    private OkHttpClient client = new OkHttpClient();

    public String run (String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public String FetchCovers(String trackname, String endpoint){
        String coverUrl = null;
        String nospaceTrackname = trackname.replace(" ", "_");
        endpoint = endpoint+ nospaceTrackname;
        try{
            coverUrl = Uri.parse(endpoint).buildUpon().build().toString();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        if(coverUrl != null) return coverUrl;
        return null;
    }
}
