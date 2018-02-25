package com.helagone.airelibre.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.helagone.airelibre.R;
import com.helagone.airelibre.datafetch.CurrentMetadataFetcher;
import com.helagone.airelibre.datamodel.TrackModel;
import com.helagone.airelibre.utility.Shoutcast;
import com.helagone.airelibre.utility.ShoutcastHelper;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    String _the_time;

    Boolean yesno = true;
    Boolean isready = false;

    public ArrayList<TrackModel> oneTrackModels =  new ArrayList<>();

    Typeface custom_font;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AlMusicaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlMusicaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlMusicaFragment newInstance(String param1, String param2) {
        AlMusicaFragment fragment = new AlMusicaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        shoutcasts = ShoutcastHelper.retrieveShoutcasts(getActivity());
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

        gotoPlaylist.setTypeface(custom_font);
        artistName.setTypeface(custom_font);
        time_remain.setTypeface(custom_font);
        time_duration.setTypeface(custom_font);
        spacer_pipe.setTypeface(custom_font);

        mProgressBar.setColor( ContextCompat.getColor(getActivity(), R.color.bright_teal) );
        mProgressBar.setProgressBarWidth(10);
        mProgressBar.setProgress(0);
        mProgressBar.setProgressWithAnimation(100, 5000);

        //HANDLING TIME OF THE DAY

        if(limit > 1800 || limit < 700 ){
            //Log.d("limit", String.valueOf(limit));
            fragmentView.setBackground(getResources().getDrawable(R.color.dark));

            gotoPlaylist.setTextColor(getResources().getColor(R.color.blanco));
            artistName.setTextColor(getResources().getColor(R.color.cool_grey));
            time_duration.setTextColor(getResources().getColor(R.color.cool_grey));
            time_remain.setTextColor(getResources().getColor(R.color.cool_grey));
            spacer_pipe.setTextColor(getResources().getColor(R.color.cool_grey));

            try{
                ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.dark));
            }catch(NullPointerException nex){
                nex.printStackTrace();
            }
        }

        handler.postDelayed(runnable, 10);

        trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(yesno){
                    trigger.setBackground(getResources().getDrawable(R.color.transparente));
                    trigger.setImageResource(R.color.transparente);
                    yesno = false;
                }else{
                    trigger.setBackground(getResources().getDrawable(R.drawable.ic_album_inside_circle));
                    trigger.setImageResource(R.drawable.ic_play_arrow_black);
                    yesno = true;
                }


                shoutcasts = ShoutcastHelper.retrieveShoutcasts(getActivity());
                //artistName.setText(shoutcasts.get(0).getName());
                streamURL = shoutcasts.get(0).getUrl();

                //SENDING STRING URL TO ACTIVITY @ radioManager -> playOrPause
                mListener.onFragmentInteraction(streamURL);
            }
        });

        if(getActivity() != null){
            if(getActivity() != null){
                RequestOptions options = new RequestOptions();
                options.circleCrop();
                Glide.with(getActivity()).load(coverUrl).apply(options).into(coverart);
            }
        }


        // Inflate the layout for this fragment
        return fragmentView;
    }//END ON CREATE

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

            custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/WorkSans-Regular.ttf");

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
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


                Log.d("current_in_millis", String.valueOf(current_in_millis));
                Log.d("current_in_millis", String.valueOf(startTime_in_millis));


                duration_in_millis = Integer.parseInt(duration)*1000;
                tiempo_transcurrido = current_in_millis - startTime_in_millis;
                loquefalta = duration_in_millis - tiempo_transcurrido;

                isready = tiempo_transcurrido != 0;

                //SAVING DURATION TO SHARED PREFERENCES
                SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //CLEARING SHARED PREFERENCES
                editor.clear().apply();
                //PUTTING INT DURATION TO SHARED PREFERENCES
                editor.putInt("transcurrido", tiempo_transcurrido);
                editor.putInt("loquefalta", loquefalta);
                editor.putInt("duracion", duration_in_millis);
                editor.apply();

                //LOGGING DATA TO CONSOLE
                /*
                Log.d("artist", artist_name);
                Log.d("track", track_name);
                Log.d("album", album_name);
                Log.d("duracion", String.valueOf(duration_in_millis));
                Log.d("start", start_time);
                */
                Log.d("transcurrido",String.valueOf(tiempo_transcurrido));
                Log.d("loquefalta > ", String.valueOf(loquefalta));


            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

            return null;
        }//END DOING IN BACKGROUND


        @Override
        protected void onPostExecute(CurrentMetadataFetcher result) {

            String lbl_trackDuration = String.format("%02d:%02d", (duration_in_millis/1000) / 60, (duration_in_millis/1000) % 60);
            //String lbl_trackremain = String.format("%02d:%02d", (tiempo_transcurrido/1000) / 60, (tiempo_transcurrido/1000) % 60);

            artistName.setText(artist_name);
            gotoPlaylist.setText(track_name);
            time_duration.setText(lbl_trackDuration);
            //t_remaining.setText(lbl_trackremain);

            if(getActivity() != null){
                RequestOptions options = new RequestOptions();
                options.circleCrop();
                Glide.with(getActivity()).load(coverUrl).apply(options).into(coverart);
            }
        }//END ON POST EXECUTE
    }//END ASYNC TASK

    /**
     * RUNNABLE UPDATE METADATA IN UI
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(getActivity() != null){
                SharedPreferences cr_sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                int cr_loquefalta =  cr_sharedPreferences.getInt("loquefalta", 2000);
                new MetadataFetcher().execute();
                handler.postDelayed(runnable, cr_loquefalta);
            }
        }
    };

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
