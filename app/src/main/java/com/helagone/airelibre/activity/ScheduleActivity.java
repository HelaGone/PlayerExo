package com.helagone.airelibre.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helagone.airelibre.MainActivity;
import com.helagone.airelibre.R;
import com.helagone.airelibre.datafetch.ProgramFetcher;
import com.helagone.airelibre.datamodel.DataModel;
import com.helagone.airelibre.interaction.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    CollapsingToolbarLayout collapsingToolbarLayout;
    private List<DataModel> trackItem_list = new ArrayList<>();
    RecyclerView recyclerView;
    Toolbar toolbar;
    Drawable menu_close;
    ImageButton btnClose;

    SharedPreferences sharedPreferences;
    String prev_prefs;
    String tracksStr;
    String cleanEachObj;
    JSONObject otroObj;
    JSONArray nwJsonArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        menu_close = getResources().getDrawable(R.drawable.ic_close);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.id_scedule_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));


        for(int i=0; i<toolbar.getChildCount(); i++){
            final View v = toolbar.getChildAt(i);
            if(v instanceof ImageButton) {
                ((ImageButton)v).setImageDrawable(menu_close);
            }
        }


        sharedPreferences = this.getSharedPreferences("tracksSet", Context.MODE_PRIVATE);
        prev_prefs = sharedPreferences.getString("trackSet", null);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!sharedPreferences.getAll().isEmpty()){
            String[] previousJson = prev_prefs.split("([}])");
            nwJsonArray = new JSONArray();
            for( int i=0; i<previousJson.length; i++){
                String eachObj = previousJson[i]+"}";
                cleanEachObj = eachObj.replace(",{", "{");
                nwJsonArray.put(cleanEachObj); //<-- objs inside nwJsonArray
            }
            for(int j=0; j<nwJsonArray.length(); j++){
                try {
                    otroObj = new JSONObject( (String) nwJsonArray.get(j) );
                    //Log.d("aNme", otroObj.get("_artistName").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //MANAGE ITEM CLICK
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //SET IMAGE BUTTON
                ImageButton favHeart = (ImageButton) view.findViewById(R.id.ic_like_heart);
                favHeart.setImageResource(R.drawable.ic_icon_heart);

                //GETTING TRACK ELEMENTS
                TextView txV_duration = (TextView) view.findViewById(R.id.prog_trackDur);
                TextView txV_trackName = (TextView) view.findViewById(R.id.prog_track);
                TextView txV_artistName = (TextView) view.findViewById(R.id.prog_artist);

                String tx_trackArtist = (String) txV_artistName.getText();
                String tx_trackName = (String) txV_trackName.getText();
                String tx_trackDuration = (String) txV_duration.getText();

                //ELEMENTS TO SAVE
                //Log.d("tosave > ", txV_artistName.getText()+" - "+txV_trackName.getText()+" - "+txV_duration.getText());

                if( sharedPreferences.getAll().isEmpty() ){
                    //Log.d("shp", "Está vacío");
                    JSONObject jsonObjectTrack =  new JSONObject();

                    try{
                        jsonObjectTrack.put("_artistName", tx_trackArtist);
                        jsonObjectTrack.put("_trackName", tx_trackName);
                        jsonObjectTrack.put("_trackDur", tx_trackDuration);
                        tracksStr += jsonObjectTrack.toString();
                        //Log.d("tracksStr", tracksStr);
                        String cleanToUp = tracksStr.replace("null", "");
                        //Log.d("cleanToUp", cleanToUp);
                        editor.putString("trackSet", cleanToUp).apply();
                        String retTrackSet = sharedPreferences.getString("trackSet", null);
                        //Log.d("retTrackSet", retTrackSet);

                    }catch(JSONException jsex){
                        jsex.printStackTrace();
                    }

                }else{
                    //Log.d("shp", "No está vacío");
                    String trackSet = sharedPreferences.getString( "trackSet", null);
                    //Log.d("trackset > here", trackSet);
                    String replaceTrackSet = trackSet.replace("null", "");
                    //Log.d("replaceTrackSet > ", replaceTrackSet);
                    JSONObject newjsonObjectTrack =  new JSONObject();
                    try{
                        newjsonObjectTrack.put("_artistName", tx_trackArtist);
                        newjsonObjectTrack.put("_trackName", tx_trackName);
                        newjsonObjectTrack.put("_trackDur", tx_trackDuration);
                        replaceTrackSet += ","+newjsonObjectTrack;
                        editor.putString("trackSet", replaceTrackSet).apply();
                        //Used just for testing . . .
                        //String nTrackSet = sharedPreferences.getString("trackSet", null);
                        //Log.d("newTrackSet", nTrackSet);
                    }catch(JSONException jsex){
                        jsex.printStackTrace();
                    }
                }
                //END SHARED PREFERENCES TEST
            }
        }));



        new FetchMetadataTask().execute();
    }


    @Override public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case android.R.id.home :
                Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        //measuring for alpha
        int toolBarHeight = toolbar.getMeasuredHeight();
        int appBarHeight = appBarLayout.getMeasuredHeight();
        Float f = ((((float) appBarHeight - toolBarHeight) + verticalOffset) / ( (float) appBarHeight - toolBarHeight)) * 255;

    }


    //SETUP ADAPTER
    public void setupAdapter(){
        recyclerView.setAdapter(new ItemAdapter(trackItem_list));
    }


    //VIEW HOLDER
    public class ItemHolder extends RecyclerView.ViewHolder{

        public TextView itArtist, itTitle, itTrackDur, itAlbum;
        public ImageView itNowPlaying;
        public ImageButton itFavorite;
        public RelativeLayout itList;

        public ItemHolder(View itemView) {
            super(itemView);
            itArtist = (TextView) itemView.findViewById(R.id.prog_artist);
            itTitle = (TextView) itemView.findViewById(R.id.prog_track);
            itTrackDur = (TextView) itemView.findViewById(R.id.prog_trackDur);
            itFavorite = (ImageButton) itemView.findViewById(R.id.ic_like_heart);
            itNowPlaying = (ImageView) itemView.findViewById(R.id.prog_imageView);
            itList = (RelativeLayout) itemView.findViewById(R.id.prog_row_item);

            itArtist.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/WorkSans-Regular.ttf"));
            itTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/WorkSans-Regular.ttf"));
            itTrackDur.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/WorkSans-Regular.ttf"));

        }
    }
    //END ITEM HODER


    public class ItemAdapter extends RecyclerView.Adapter<ItemHolder>{
        private List<DataModel> trackItems;

        public ItemAdapter(List<DataModel> theTracks){
            trackItems = theTracks;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prog_list_row, parent, false);
            return new ItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            //Log.d("execution", "onBindExecution "+position);
            DataModel dataModel= trackItems.get(position);

            //Log.d("execution here >>>>>>>", String.valueOf(nwJsonArray.length() ) );

            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.END_OF, R.id.prog_imageView);

            final RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.END_OF, R.id.prog_imageView);
            params2.addRule(RelativeLayout.BELOW, R.id.prog_track);

            if(position % 2 == 0){
                holder.itList.setBackgroundColor(getResources().getColor(R.color.duck_egg_blue_pale));
            }else{
                holder.itList.setBackgroundColor(getResources().getColor(R.color.blanco));
            }

            holder.itTitle.setText(dataModel.getAlbumName());
            holder.itTitle.setTextSize(16);
            holder.itArtist.setText(dataModel.getArtistName());
            holder.itFavorite.setImageResource(R.drawable.ic_icon_heart_empty);

            //Comprobar cuáles títulos están en favoritos

            if(position == 0){
                holder.itNowPlaying.setVisibility(View.VISIBLE);
                holder.itTrackDur.setVisibility(View.GONE);
                holder.itTitle.setLayoutParams(params);
                holder.itArtist.setLayoutParams(params2);
                holder.itNowPlaying.setImageResource(R.drawable.ic_icon_soundwave);
            }else{


                //int durInFormat = Integer.parseInt(dataModel.getDuration());
                //String lbl_trackELapsed = String.format("%02d:%02d", durInFormat / 60, durInFormat % 60);
                holder.itNowPlaying.setVisibility(View.GONE);
                holder.itTrackDur.setVisibility(View.VISIBLE);
                holder.itTrackDur.setText(dataModel.getDuration());

            }
        }

        @Override
        public int getItemCount() {
            return trackItems.size();
        }
    }
    //END ITEM ADAPTER


    public class FetchMetadataTask extends AsyncTask< Void, Void, List<DataModel> > {

        @Override
        protected List<DataModel> doInBackground(Void... params) {
            return new ProgramFetcher().fetchItems();
        }

        @Override
        protected void onPostExecute(List<DataModel> items){
            trackItem_list = items;
            setupAdapter();
        }
    }
    //END FETCH METADATA TASK



}
