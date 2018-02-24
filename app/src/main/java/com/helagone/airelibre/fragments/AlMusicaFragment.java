package com.helagone.airelibre.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.helagone.airelibre.R;
import com.helagone.airelibre.utility.Shoutcast;
import com.helagone.airelibre.utility.ShoutcastHelper;

import java.util.ArrayList;
import java.util.List;

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
    TextView textView;
    ImageView coverart;

    String coverUrl = "http://lisa.mx/airelibre/al_imagenes/art-00.jpg";
    String streamURL;

    private List<Shoutcast> shoutcasts = new ArrayList<>();




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

        Log.d("shoutcasts >>>>>>>>>> ", String.valueOf(shoutcasts.get(0).getName()));

        trigger = fragmentView.findViewById(R.id.id_trigger);
        textView = fragmentView.findViewById(R.id.lbl_trackinfo);
        coverart = fragmentView.findViewById(R.id.imgAlbum);


        trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shoutcasts = ShoutcastHelper.retrieveShoutcasts(getActivity());
                textView.setText(shoutcasts.get(0).getName());
                streamURL = shoutcasts.get(0).getUrl();
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
