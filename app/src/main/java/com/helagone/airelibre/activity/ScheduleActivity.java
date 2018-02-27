package com.helagone.airelibre.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
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
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private List<DataModel> trackItem_list = new ArrayList<>();
    RecyclerView recyclerView;
    Toolbar toolbar;
    Drawable menu_close;

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
