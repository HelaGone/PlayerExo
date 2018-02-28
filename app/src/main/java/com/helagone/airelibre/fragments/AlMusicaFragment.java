package com.helagone.airelibre.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.helagone.airelibre.MainActivity;
import com.helagone.airelibre.R;
import com.helagone.airelibre.activity.ScheduleActivity;
import com.helagone.airelibre.datafetch.CurrentMetadataFetcher;
import com.helagone.airelibre.datamodel.TrackModel;
import com.helagone.airelibre.service.RadioManager;
import com.helagone.airelibre.utility.Shoutcast;
import com.helagone.airelibre.utility.ShoutcastHelper;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlMusicaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlMusicaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlMusicaFragment extends Fragment {


    ImageButton trigger;
    ImageView coverart;

    Button gotoPlaylist;
    ToggleButton likeEmpty;
    TextView artistName;
    TextView time_remain;
    TextView time_duration;
    TextView spacer_pipe;

    String coverUrl = "http://lisa.mx/airelibre/al_imagenes/art-00.jpg";
    String streamURL;
    String title_artist;
    String trackTitle;
    String album;
    String cover_endpoint;
    String trackDur;
    String startTime;

    //FETCH METADATA ///////////////////////////
    String lametadata;
    String[] meta_parts;

    String artist_name;
    String track_name;
    String album_name;
    String duration;
    String start_time;

    int loquefalta;
    int duration_in_millis;
    int tiempo_transcurrido;
    int transcurrido;
    int durarcion;
    int int_elapsed;
    int int_duration;
    int l_transc;
    private Handler handler = new Handler();

    private List<Shoutcast> shoutcasts = new ArrayList<>();

    CircularProgressBar mProgressBar;

    Date d_current_date;
    SimpleDateFormat thedateFormat =  new SimpleDateFormat("HH:mm", Locale.getDefault());
    public int limit;

    Boolean isready = false;
    Boolean addTo = false;

    public ArrayList<TrackModel> oneTrackModels =  new ArrayList<>();

    Typeface custom_font;
    Typeface ws_semibold;

    MetadataFetcher metadataFetcher;
    RadioManager radioManager;
    Drawable menu_night;
    Toolbar toolbar;

    SharedPreferences shPreferences;
    SharedPreferences.Editor editor;
    String sharedString;

    String _the_time;
    String tracksStr;

    DefaultTimeBar defaultTimeBar;

    int count; //For progress bar
    int transc_to_cent;
    Timer timer;



    private OnFragmentInteractionListener mListener;

    public AlMusicaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AlMusicaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlMusicaFragment newInstance() {
        return new AlMusicaFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //get arguments
        }

        shoutcasts = ShoutcastHelper.retrieveShoutcasts(getActivity());
        radioManager = new RadioManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_al_musica, container, false);

        trigger = fragmentView.findViewById(R.id.id_trigger);
        coverart = fragmentView.findViewById(R.id.imgAlbum);
        mProgressBar = fragmentView.findViewById(R.id.circularprogressBar);

        gotoPlaylist = fragmentView.findViewById(R.id.btn_to_playlist);
        artistName = fragmentView.findViewById(R.id.lbl_artistName);
        time_remain = fragmentView.findViewById(R.id.lbl_timeremain);
        time_duration = fragmentView.findViewById(R.id.lbl_trDur);
        spacer_pipe = fragmentView.findViewById(R.id.lbl_item_separador);
        likeEmpty = fragmentView.findViewById(R.id.likeEmpty);

        gotoPlaylist.setTypeface(ws_semibold);
        artistName.setTypeface(custom_font);
        time_remain.setTypeface(custom_font);
        time_duration.setTypeface(custom_font);
        spacer_pipe.setTypeface(custom_font);


        SharedPreferences sh_prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        //handler.postDelayed(progressbar_update, 1000);
        handler.postDelayed(runnable, sh_prefs.getInt("loquefalta", 2000));





        //HANDLING TIME OF THE DAY

        if(limit > 1800 || limit < 700 ){
            //Log.d("limit", String.valueOf(limit));
            fragmentView.setBackground(getResources().getDrawable(R.color.dark));

            gotoPlaylist.setTextColor(getResources().getColor(R.color.blanco));
            artistName.setTextColor(getResources().getColor(R.color.cool_grey));
            time_duration.setTextColor(getResources().getColor(R.color.cool_grey));
            time_remain.setTextColor(getResources().getColor(R.color.cool_grey));
            spacer_pipe.setTextColor(getResources().getColor(R.color.cool_grey));

            menu_night = getResources().getDrawable(R.drawable.ic_menu_night);

            try{
                ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.dark));
            }catch(NullPointerException nex){
                nex.printStackTrace();
            }
        }

        toolbar = getActivity().findViewById(R.id.toolbar);
        menu_night = getResources().getDrawable(R.drawable.ic_menu_night);

        for(int i=0; i<toolbar.getChildCount(); i++){
            final View v = toolbar.getChildAt(i);

            if(v instanceof ImageButton) {
                ((ImageButton)v).setImageDrawable(menu_night);
            }
        }


        SharedPreferences dw_sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        //int sh_loquefalta = dw_sharedPrefs.getInt("loquefalta", 2000);
        int sh_transcurrido = dw_sharedPrefs.getInt("transcurrido", 2000);
        int sh_duracion = dw_sharedPrefs.getInt("duracion", 2000);
        transc_to_cent = sh_transcurrido*100/sh_duracion;

        Log.d("tres >>", String.valueOf(transc_to_cent) );


        trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioManager != null ){
                    if(radioManager.isPlaying()){
                        trigger.setBackground(getResources().getDrawable(R.drawable.ic_album_inside_circle));
                        trigger.setImageResource(R.drawable.ic_play_arrow_black);
                    }else{
                        trigger.setBackground(getResources().getDrawable(R.color.transparente));
                        trigger.setImageResource(R.color.transparente);
                    }
                }

                shoutcasts = ShoutcastHelper.retrieveShoutcasts(getActivity());
                //artistName.setText(shoutcasts.get(0).getName());
                streamURL = shoutcasts.get(1).getUrl();

                //SENDING STRING URL TO ACTIVITY @ radioManager -> playOrPause
                mListener.onFragmentInteraction(streamURL);
            }
        });

        gotoPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ScheduleActivity.class));
            }
        });

        if(getActivity() != null){
            if(getActivity() != null){
                RequestOptions options = new RequestOptions();
                options.circleCrop();
                Glide.with(getActivity()).load(coverUrl).apply(options).into(coverart);
            }
        }


        /*
         *  CLICK TO UP VOTE
         */
        likeEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("like_click", "click here");
                //VARIABLES
                String _trackArtist = (String) artistName.getText();
                String _trackName = (String) gotoPlaylist.getText();
                String _trackAlbumName = (String) gotoPlaylist.getText();
                String _trackDuration = (String) time_duration.getText();

                shPreferences = getActivity().getSharedPreferences("tracksSet", Context.MODE_PRIVATE);
                sharedString = shPreferences.getString("trackSet", null);
                editor = shPreferences.edit();

                //ANALYTICS
                /*Bundle bundle4 = new Bundle();
                bundle4.putString(FirebaseAnalytics.Param.ITEM_NAME, _trackName);
                bundle4.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "music_like");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle4);*/
                //END VARIABLES

                if( shPreferences.getAll().isEmpty() ){
                    //Log.d("shp", "Está vacío");
                    JSONObject jsonObjectTrack =  new JSONObject();
                    try{
                        jsonObjectTrack.put("_artistName", _trackArtist);
                        jsonObjectTrack.put("_trackName", _trackName);
                        jsonObjectTrack.put("_trackDur", _trackDuration);
                        tracksStr += jsonObjectTrack.toString();
                        String cleanToUp = tracksStr.replace("null", "");
                        //Log.d("cleanToUp", cleanToUp);
                        editor.putString("trackSet", cleanToUp).apply();

                        String retTrackSet = shPreferences.getString("trackSet", null);
                        //Log.d("retTrackSet", retTrackSet);

                    }catch(JSONException jsex){
                        jsex.printStackTrace();
                    }
                }else{
                    String trackSet = shPreferences.getString( "trackSet", null);
                    String replaceTrackSet = trackSet.replace("null", "");
                    String[] arr_str = replaceTrackSet.split("([}])");
                    for(String s: arr_str){
                        String cleanStr = s.replace(",{", "{");
                        try {
                            JSONObject oneObj = new JSONObject(cleanStr+"}");
                            if(oneObj.getString("_artistName").equals(_trackArtist) && oneObj.getString("_trackName").equals(_trackName)){
                                addTo = false;
                            }else{
                                addTo = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONObject newjsonObjectTrack =  new JSONObject();
                    try{
                        newjsonObjectTrack.put("_artistName", _trackArtist);
                        newjsonObjectTrack.put("_trackName", _trackName);
                        newjsonObjectTrack.put("_trackDur", _trackDuration);
                        if(addTo){
                            replaceTrackSet += ","+newjsonObjectTrack;
                            editor.putString("trackSet", replaceTrackSet).apply();
                        }
                    }catch(JSONException jsex){
                        jsex.printStackTrace();
                    }
                }//END SHARED PREFERENCES TEST
            }
        });//END ON CLICK LISTENER LIKE EMPTY



        timer = new Timer();
        timer.schedule(new timerUpdTask(), 0, 1000);



        // Inflate the layout for this fragment
        return fragmentView;
    }//END ON CREATE




    /**
     * RUNNABLE UPDATE METADATA IN UI
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("Runnable", "---------------RUN------------");
            if(getActivity() !=  null){
                SharedPreferences cr_sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                int cr_loquefalta =  cr_sharedPreferences.getInt("loquefalta", 2000);
                new MetadataFetcher().execute();
                handler.postDelayed(runnable, cr_loquefalta);
                Log.d("Loquefalta > 1", String.valueOf( cr_loquefalta ));
                Log.d("Runnable", "---------------RUN------------");
            }
        }
    };


    private class timerUpdTask extends TimerTask{
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updTimer();
                }
            });
        }
    }

    private void updTimer (){
        tiempo_transcurrido++;
        Log.d("transc", String.valueOf(tiempo_transcurrido));
        time_remain.setText(String.format(Locale.getDefault(),"%02d:%02d", tiempo_transcurrido/1000/60, tiempo_transcurrido/1000 % 60 ));
    }

















    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

            new MetadataFetcher().execute();

            custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/WorkSans-Regular.ttf");
            ws_semibold    = Typeface.createFromAsset(getActivity().getAssets(), "fonts/WorkSans-SemiBold.ttf");

            //TODAY CALC
            d_current_date = new Date();
            _the_time = thedateFormat.format(d_current_date);
            String twentyfour = _the_time.replace(":", "");
            limit =  Integer.parseInt(twentyfour);


        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //handler.removeCallbacks(progressbar_update);
        handler.removeCallbacks(runnable);
        timer.cancel();
        timer.purge();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(radioManager != null){
            if( radioManager.isPlaying() ){
                trigger.setBackground(getResources().getDrawable(R.color.transparente));
                trigger.setImageResource(R.color.transparente);
            }else{
                trigger.setBackground(getResources().getDrawable(R.drawable.ic_album_inside_circle));
                trigger.setImageResource(R.drawable.ic_play_arrow_black);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        //handler.removeCallbacks(progressbar_update);
        handler.removeCallbacks(runnable);
        timer.cancel();
        timer.purge();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //handler.removeCallbacks(progressbar_update);
        handler.removeCallbacks(runnable);
        timer.cancel();
        timer.purge();
    }




    /**
     * METADATA FETCHER
     */
    private class MetadataFetcher extends AsyncTask<URL, Void, CurrentMetadataFetcher> {

        @Override
        protected CurrentMetadataFetcher doInBackground(URL... urls) {
            CurrentMetadataFetcher fetchMetadata = new CurrentMetadataFetcher();


            try {
                //GET CURRENT DATE
                Date currentDateTime = new Date();
                int current_in_millis = (int) currentDateTime.getTime();


                //SAVING TRACK MODEL
                /*
                oneTrackModels.add(new TrackModel(trackTitle, album, title_artist));
                StorageUtil storage = new StorageUtil( getActivity().getApplicationContext() );
                storage.storeAudio( oneTrackModels);
                */


                //FETCHING METADATA FROM URL
                lametadata = fetchMetadata.run("http://ec2-18-144-70-48.us-west-1.compute.amazonaws.com:8000/currentsong?sid=1");
                meta_parts = lametadata.split("_-_");
                artist_name = meta_parts[0];
                track_name = meta_parts[1];
                album_name = meta_parts[2];
                duration = meta_parts[3];
                start_time = meta_parts[4];


                //Get Cover art url
                cover_endpoint = "http://lisa.mx/airelibre/al_imagenes/art-00.jpg?";
                //cover_endpoint = setupCoverEndpoint();
                coverUrl = fetchMetadata.FetchCovers(track_name, cover_endpoint);


                //DATE OPERATIONS TO GET TRACK REMAINING TIME
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date startTime_date = formatter.parse(start_time);
                int startTime_in_millis = (int) startTime_date.getTime();


                /*Log.d("current_in_millis", String.valueOf( Math.abs(current_in_millis) ));
                Log.d("current_in_millis", String.valueOf( Math.abs(startTime_in_millis) ));*/


                duration_in_millis = Integer.parseInt(duration)*1000;
                tiempo_transcurrido = current_in_millis - startTime_in_millis;
                loquefalta = duration_in_millis - tiempo_transcurrido;

                isready = tiempo_transcurrido != 0;

                //SAVING DURATION TO SHARED PREFERENCES
                if(getActivity() != null){
                    SharedPreferences sharedPreferences_trackinfo = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences_trackinfo.edit();
                    //CLEARING SHARED PREFERENCES
                    editor.clear().apply();
                    //PUTTING INT DURATION TO SHARED PREFERENCES
                    editor.putInt("transcurrido", tiempo_transcurrido);
                    editor.putInt("loquefalta", loquefalta);
                    editor.putInt("duracion", duration_in_millis);
                    editor.apply();
                }


                //LOGGING DATA TO CONSOLE
                /*
                Log.d("artist", artist_name);
                Log.d("track", track_name);
                Log.d("album", album_name);
                Log.d("duracion", String.valueOf(duration_in_millis));
                Log.d("start", start_time);
                Log.d("transcurrido",String.valueOf(tiempo_transcurrido));
                Log.d("loquefalta > ", String.valueOf(loquefalta));
                */

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

            return null;
        }//END DOING IN BACKGROUND


        @Override
        protected void onPostExecute(CurrentMetadataFetcher result) {

            if(getActivity() !=  null){
                SharedPreferences cr_sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                int cr_loquefalta =  cr_sharedPreferences.getInt("loquefalta", 2000);
                int cr_transcurrido = cr_sharedPreferences.getInt("transcurrido", 2000);
                int cr_duracion = cr_sharedPreferences.getInt("duracion", 2000);

                int topercent = cr_transcurrido*100/cr_duracion;

                mProgressBar.setColor( ContextCompat.getColor(getActivity(), R.color.bright_teal) );
                mProgressBar.setProgressBarWidth(10);
                mProgressBar.setProgress(topercent);
                mProgressBar.setProgressWithAnimation(100, Math.abs(cr_loquefalta) );

                Log.d("Loquefalta_aqui >>", String.valueOf(  (cr_loquefalta - cr_transcurrido)  ));
            }


            String lbl_trackDuration = String.format("%02d:%02d", (duration_in_millis/1000) / 60, (duration_in_millis/1000) % 60);
            String lbl_trackremain = String.format("%02d:%02d", (tiempo_transcurrido/1000) / 60, (tiempo_transcurrido/1000) % 60);

            artistName.setText(artist_name);
            gotoPlaylist.setText(track_name);
            time_duration.setText(lbl_trackDuration);


            //time_remain.setText(lbl_trackremain);





            if(getActivity() != null){
                RequestOptions options = new RequestOptions();
                options.circleCrop();
                Glide.with(getActivity()).load(coverUrl).apply(options).into(coverart);
            }
        }//END ON POST EXECUTE
    }//END ASYNC TASK














    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String str_url);
    }
}
