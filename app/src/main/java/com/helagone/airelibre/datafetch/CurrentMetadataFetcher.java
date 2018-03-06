package com.helagone.airelibre.datafetch;

import android.net.Uri;
import android.util.Log;

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

        Log.d("larespuesta", String.valueOf(response));

        if(String.valueOf(response).equals( "" )){
            return "";
        }else{
            return response.body().string();

        }
    }

    public String FetchCovers(String endpoint){
        String coverUrl = null;
        endpoint = endpoint+"?"+System.currentTimeMillis();
        try{
            coverUrl = Uri.parse(endpoint).buildUpon().build().toString();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        if(coverUrl != null) return coverUrl;
        return null;
    }
}
