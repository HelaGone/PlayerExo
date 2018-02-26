package com.helagone.airelibre;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.helagone.airelibre.activity.ScheduleActivity;
import com.helagone.airelibre.activity.TermsActivity;
import com.helagone.airelibre.fragments.AlMusicaFragment;
import com.helagone.airelibre.fragments.ContactoFragment;
import com.helagone.airelibre.fragments.FavoritosFragment;
import com.helagone.airelibre.fragments.UsFragment;
import com.helagone.airelibre.service.PlaybackStatus;
import com.helagone.airelibre.service.RadioManager;
import com.helagone.airelibre.utility.Shoutcast;
import com.helagone.airelibre.utility.ShoutcastHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements
        AlMusicaFragment.OnFragmentInteractionListener,
        ContactoFragment.OnFragmentInteractionListener,
        FavoritosFragment.OnFragmentInteractionListener,
        UsFragment.OnFragmentInteractionListener,
        View.OnClickListener{

    //Drawer Variables
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar toolbar;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_ALMUSICA= "almusica";
    private static final String TAG_ALCDMX = "alcdmx";
    private static final String TAG_ALTULUM = "movies";
    private static final String TAG_SCHEDULE = "schedule";
    private static final String TAG_FAVS = "favourites";
    private static final String TAG_US = "nosotros";
    private static final String TAG_CONTACT = "contacto";
    private static final String TAG_TERMS = "terms";

    public static String CURRENT_TAG = TAG_ALMUSICA;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    //Player Variables
    RadioManager radioManager;

    LayoutInflater linearLayout;

    LinearLayout almusic;
    LinearLayout terms;
    TextView lbl_emisoras;

    TextView titleToolbar;

    //TIME SHIFT
    Date d_current_date;
    SimpleDateFormat thedateFormat =  new SimpleDateFormat("HH:mm", Locale.getDefault());
    public int limit;
    String _the_time;

    Typeface custom_font;
    Typeface ws_semibold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/WorkSans-Regular.ttf");
        ws_semibold = Typeface.createFromAsset(getAssets(), "fonts/WorkSans-SemiBold.ttf");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleToolbar = toolbar.findViewById(R.id.titleToolbar);
        titleToolbar.setTextColor(getResources().getColor(R.color.cool_grey));
        titleToolbar.setTypeface(custom_font);


        mHandler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        linearLayout = getLayoutInflater();

        almusic = navigationView.getHeaderView(0).findViewById(R.id.id_nav_almusic);
        lbl_emisoras = navigationView.getHeaderView(0).findViewById(R.id.lbl_emisoras);
        terms = navigationView.findViewById(R.id.menu_item_terms);

        lbl_emisoras.setTypeface(ws_semibold);

        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.toolbar_bg));



        /**
         * TIME SHIFT
         */
        //TODAY CALC
        d_current_date = new Date();
        _the_time = thedateFormat.format(d_current_date);
        String twentyfour = _the_time.replace(":", "");
        limit =  Integer.parseInt(twentyfour);


        if(limit > 1800 || limit < 700 ){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.dark));


            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.dark));
            titleToolbar.setTextColor(getResources().getColor(R.color.cool_grey));
            try{
                ((AppCompatActivity) this).getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.dark));
            }catch(NullPointerException nex){
                nex.printStackTrace();
            }
        }else{
            titleToolbar.setTextColor(getResources().getColor(R.color.dark_grey_blue));
        }


        almusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_TAG = TAG_ALMUSICA;
                navItemIndex = 0;
                loadHomeFragment();
                drawer.closeDrawers();
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TermsActivity.class));
                drawer.closeDrawers();
            }
        });

        navHeader = navigationView.getHeaderView(0);

        TextView emisoras = navHeader.findViewById(R.id.lbl_emisoras);
        emisoras.setTypeface(custom_font);


        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_ALMUSICA;
            loadHomeFragment();
        }


        //PLAYER
        radioManager = new RadioManager(this);

    }


    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            //toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        //toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }


    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                return new AlMusicaFragment();
            case 1:
                return new AlMusicaFragment();
            case 2:
                return new AlMusicaFragment();
            case 4:
                return new FavoritosFragment();
            case 5:
                return new UsFragment();
            case 6:
                return new ContactoFragment();
            default:
                return new AlMusicaFragment();
        }
    }


    private void setToolbarTitle() {

        TextView titleTollbar = findViewById(R.id.titleToolbar);
        titleTollbar.setText(activityTitles[navItemIndex]);

        //getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        //navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_schedule_item:
                        startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
                        drawer.closeDrawers();
                        return  true;
                    case R.id.nav_favs_item:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_FAVS;
                        break;
                    case R.id.nav_nosotros_item:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_US;
                        break;
                    case R.id.nav_contacto_item:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_CONTACT;
                        break;
                    /*case R.id.nav_terms_id:
                        startActivity(new Intent(MainActivity.this, TermsActivity.class));
                        drawer.closeDrawers();
                        return true;*/
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                /*
                if (menuItem.isChecked()) {
                    menuItem.setChecked(true);
                } else {
                    menuItem.setChecked(true);
                }*/
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String app_store_url = "playstore.com";
        switch(item.getItemId()){
            case R.id.action_share:
                ShareThis("Descarga airelibre: ", app_store_url);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ShareThis(String title, String message) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(sendIntent.EXTRA_SUBJECT, title);
        sendIntent.putExtra(sendIntent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Compartir"));
    }//END SHARE THIS



    @Override
    public void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public void onStop(){
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    protected void onDestroy(){
        super.onDestroy();
        radioManager.unbind();
    }

    protected void onResume(){
        super.onResume();
        radioManager.bind();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_ALMUSICA;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();

        finish();
    }

    @Subscribe
    public void onEvent(String status){
        switch (status){
            case PlaybackStatus.LOADING:
                // loading
                break;
            case PlaybackStatus.ERROR:
                Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void onFragmentInteraction(String stringurl) {
        if(TextUtils.isEmpty(stringurl)) return;
        radioManager.playOrPause(stringurl);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
