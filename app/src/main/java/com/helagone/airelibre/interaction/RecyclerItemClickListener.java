package com.helagone.airelibre.interaction;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by HELA on 2/25/18.
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private AdapterView.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    public interface OnItemClickListener{
        public void onItemClick(View view, int position);
    }//ON ITEM CLICK LISTENER

    public RecyclerItemClickListener(Context context, OnItemClickListener listener){
        mListener = (AdapterView.OnItemClickListener) listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return true;
            }
        });
    }//END RECYCLER ITEM CLICK LISTENER



    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder( e.getX(), e.getY() );

        if(childView != null && mListener != null && mGestureDetector.onTouchEvent(e)){
            
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
