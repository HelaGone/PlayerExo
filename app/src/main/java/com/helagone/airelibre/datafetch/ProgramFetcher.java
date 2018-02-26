package com.helagone.airelibre.datafetch;

import android.net.Uri;

import com.helagone.airelibre.datamodel.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HELA on 2/25/18.
 */

public class ProgramFetcher {
    private DataModel dataModel;
    private static final String ERR = "metadatafetcher_err";
    private JSONObject singleTrack;

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException( connection.getResponseMessage()+ ": with " + urlSpec );
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            return outputStream.toByteArray();
        }finally{
            connection.disconnect();
        }
    }
    //END GET URL BYTES


    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }
    //END GET URL STRING


    public List<DataModel> fetchItems(){
        List<DataModel> items = new ArrayList<>();
        try{
            String url = Uri.parse("http://lisa.mx/airelibre/tracklistjson/").buildUpon().build().toString();
            String jsonString = getUrlString(url);
            JSONArray jsonArray = new JSONArray(jsonString);
            parseItems(items, jsonArray); //-->CALLING PARSE ITEMS
        }catch(IOException | JSONException ioe){
            ioe.printStackTrace();
            //Log.e(ERR, "Failed to fetch items"+ ioe);
        }
        return items;
    }//END FETCH ITEMS


    private void parseItems(List<DataModel> items, JSONArray _jsonBodyArray) throws IOException, JSONException{
        for(int i=0; i < _jsonBodyArray.length(); i++){
            singleTrack = _jsonBodyArray.getJSONObject(i);
            dataModel = new DataModel();
            dataModel = new DataModel();
            dataModel.setArtistName(singleTrack.getString("artist"));
            dataModel.setTrackName(singleTrack.getString("title"));
            dataModel.setAlbumName(singleTrack.getString("album"));
            dataModel.setDuration(singleTrack.getString("duration"));
            items.add(dataModel);
        }
        //END ITERATION
    }
    //END PARSE ITEMS
}
