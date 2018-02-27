package com.helagone.airelibre.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helagone.airelibre.R;
import com.helagone.airelibre.datamodel.FavsDataModel;
import com.helagone.airelibre.datamodel.TrackModel;
import com.helagone.airelibre.interaction.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoritosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoritosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritosFragment extends Fragment {

    RecyclerView recyclerView;
    List<FavsDataModel> listModel = new ArrayList<>();
    CollapsingToolbarLayout collapsingTL;

    String prevPrefs;
    SharedPreferences thepreferences;
    SharedPreferences.Editor editor_favs;

    AppBarLayout appBarLayout;


    private OnFragmentInteractionListener mListener;

    public FavoritosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritosFragment newInstance(String param1, String param2) {
        FavoritosFragment fragment = new FavoritosFragment();
        Bundle args = new Bundle();
        args.putString("", param1);
        args.putString("", param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        appBarLayout = getActivity().findViewById(R.id.nav_appbar);
        appBarLayout.setElevation(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if(prevPrefs != null){
            //Log.d("null", "element not null");
            View favs_view = inflater.inflate(R.layout.fragment_favoritos_list, container, false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.blanco));

            recyclerView = (RecyclerView) favs_view.findViewById(R.id.id_favoritos_list_fragment);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1) );
            setupAdapter();

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            //Log.d("upvote", "click here");
                            ImageButton favHeart = (ImageButton) view.findViewById(R.id.ic_like_heart);
                            favHeart.setImageResource(R.drawable.ic_icon_heart_empty);

                            editor_favs = thepreferences.edit();

                            //si se hace click en la la fila, obtener las entradas en el json y eliminar la entrada seleccionada.
                            prevPrefs = thepreferences.getString("trackSet", null);
                            //Log.d("upvote > > > ", String.valueOf(prevPrefs));



                            String strarr[] = prevPrefs.split("([},{]{3})");
                            int thisPosition = recyclerView.getChildAdapterPosition(view);
                            String toremove = strarr[ thisPosition ];
                            String clean1 = toremove.replace("}", "");
                            String clean2 = clean1.replace("{", "");
                            String toremove_final = "{"+clean2+"}";

                            //Log.d("toremove >> ", strarr[recyclerView.getChildAdapterPosition(view)]);


                            //Log.d("upvote > RM", toremove_final);

                            String valuesToSave = "";

                            for (String str: strarr) {
                                //Log.d("upvote > 0.1", str);
                                String _clean1 = str.replace("{", "");
                                String _clean2 = _clean1.replace("}", "");
                                String _toremove_final = "{"+_clean2+"}";

                                //Log.d("upvote > f", _toremove_final );
                                //END CLEANING STRINGS

                                if(!_toremove_final.equals(toremove_final)){
                                    //Log.d("upvote > if", "is  not equal");
                                    //Log.d("upvote > f", _toremove_final );

                                    valuesToSave += ","+_toremove_final;

                                    try {
                                        JSONObject newObject = new JSONObject(_toremove_final);
                                        //Log.d("upvote > obj", String.valueOf(newObject));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        //Log.d("upvote > exc", "exception > "+e);
                                    }

                                }//END CONDITION
                            }//END ITERATION

                            String vts_clean = valuesToSave.substring(1);
                            //Log.d("upvote > fin", vts_clean);

                            editor_favs.putString("trackSet", vts_clean).apply();



                        }//END ON ITEM CLICK
                    })
            );

            return favs_view;
        }else{
            //Log.d("null", "prevPrefs null");
            View favs_view_empty = inflater.inflate(R.layout.fragment_favoritos, container, false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.bg_view));
            return favs_view_empty;
        }

    }//END ON CREATE VIEW


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            //Log.d("execution", "on attach execution");

            //GETTING SHARED PREFERENCES
            thepreferences = getActivity().getSharedPreferences("tracksSet", Context.MODE_PRIVATE);
            prevPrefs = thepreferences.getString("trackSet", null);

            if(!thepreferences.getAll().isEmpty()){

                String[] previousJson = prevPrefs.split("([}])");
                JSONArray nwJsonArray = new JSONArray();



                for( int i=0; i<previousJson.length; i++){
                    FavsDataModel fdm = new FavsDataModel();
                    String eachObj = previousJson[i]+"}";
                    String cleanEachObj = eachObj.replace(",{", "{");
                    nwJsonArray.put(cleanEachObj); //<-- objs inside nwJsonArray
                    //Log.d("prevs", cleanEachObj);
                    JSONObject favJsonObj = null;
                    try {
                        favJsonObj = new JSONObject(cleanEachObj);
                        fdm.setTrackArtistname(favJsonObj.getString("_artistName"));
                        fdm.setTrackTitle(favJsonObj.getString("_trackName"));
                        fdm.setTrackDuration(favJsonObj.getString("_trackDur"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    listModel.add(fdm);
                }
                //Log.d("previous", String.valueOf(previousJson.length));

            }else{
                //Log.d("previous", "Not previous preferences");
            }

        } else throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");

    }
    //END ON ATTACH

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /** VIEW HOLDER */
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView _theTrackArtistName, _theTrackName, _theTrackDuration;
        public RelativeLayout mRelativeLayout;
        public ImageButton mHeart;

        public MyViewHolder(View itemView){
            super(itemView);
            _theTrackArtistName = itemView.findViewById(R.id.prog_artist);
            _theTrackName       = itemView.findViewById(R.id.prog_track);
            _theTrackDuration   = itemView.findViewById(R.id.prog_trackDur);
            mRelativeLayout     = itemView.findViewById(R.id.prog_row_item);
            mHeart              = itemView.findViewById(R.id.ic_like_heart);
        }
    }

    /**
     * ADAPTER FOR RECYCLER VIEW
     */
    public class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<FavsDataModel> _items;

        public RecyclerViewAdapter(List<FavsDataModel> items){
            _items = items;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.prog_list_row, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            //MANAGE UI FROM HERE
            FavsDataModel favsDataModel = _items.get(position);

            holder._theTrackArtistName.setText(favsDataModel.getTrackArtistname());
            holder._theTrackName.setText(favsDataModel.getTrackTitle());
            holder._theTrackDuration.setText(favsDataModel.getTrackDuration());

            if(position % 2 == 0){
                holder.mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.duck_egg_blue_pale));
            }else{
                holder.mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.blanco));
            }

            holder.mHeart.setImageResource(R.drawable.ic_icon_heart);

        }

        @Override
        public int getItemCount() {
            return _items.size();
        }
    }//END RECYCLER VIEW


    public void setupAdapter(){
        if(isAdded()){
            //Log.d("isadded", "Fragment is attached to activity");
            recyclerView.setAdapter(new RecyclerViewAdapter(listModel));
        }
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
        void onFragmentInteraction(Uri uri);
    }
}
