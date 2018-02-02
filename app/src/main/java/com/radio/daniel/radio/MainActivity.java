package com.radio.daniel.radio;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.kila.apprater_dialog.lars.AppRater;
import com.radio.daniel.radio.database.Database;
import com.radio.daniel.radio.fragments.AddRadioStationFragment;
import com.radio.daniel.radio.fragments.InfoFragment;
import com.radio.daniel.radio.fragments.PlayerFragment;
import com.radio.daniel.radio.fragments.RadioStationSelectionFragment;
import com.radio.daniel.radio.fragments.SettingsFragment;
import com.radio.daniel.radio.fragments.ToolbarFragment;
import com.radio.daniel.radio.notifications.NotificationPlayer;
import com.radio.daniel.radio.transforms.BlurTransform;
import com.squareup.picasso.Picasso;



public class MainActivity extends AppCompatActivity {

    private Radio radio = Radio.getInstance();
    private Context context;
    private boolean playerIsHidden = true;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        context = this;
        radio.setupRadio(this);

        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(new HeadSetReceiver(), filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation);
        final ImageView headerBackgroundImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.drawer_header_layout_background);
        final TextView headerStationName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_header_layout_radio_name);
        final View playerContainer = findViewById(R.id.main_player_container);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        final View transparentView = findViewById(R.id.main_transparent_layer);
        final ActionBarDrawerToggle drawerToggle;

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        new DatabaseAsyncTask(new DatabaseAsyncTask.TaskListener() {
            @Override
            public void inBackground() {
                Database.createDatabase(context);
            }

            @Override
            public void onPost() {
                if(Radio.getRadioStationList().size() == 0) {
                    Radio.setRadioStationList(Database.getRadioStations());
                    Radio.setRadioStationListOriginal(Database.getRadioStations());
                }

                if (getSupportFragmentManager().findFragmentByTag(RadioStationSelectionFragment.TAG) == null) {

                    new AppRater.StarBuilder(context, "com.radio.daniel.radio")
                            .showDefault()
                            .minimumNumberOfStars(4)
                            .email("empty@gmail.com")
                            .daysToWait(1)
                            .timesToLaunch(0)
                            .title("Rate " + getString(R.string.app_name))
                            .neverButton(getString(R.string.rate_never))
                            .notNowButton(getString(R.string.rate_later))
                            .message(getString(R.string.rate_message))
                            .appLaunched();

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                            new RadioStationSelectionFragment(), RadioStationSelectionFragment.TAG).commit();

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_player_container,
                            new PlayerFragment(), PlayerFragment.TAG).commit();

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_header_container,
                            new ToolbarFragment(), ToolbarFragment.TAG).commit();

                }


                if(!Radio.getRadioStationListOriginal().get(0).getImageURL().equals("")) {
                    Picasso.with(context).load(Radio.getRadioStationListOriginal().get(0).getImageURL()).transform(new BlurTransform(context)).into(headerBackgroundImage);
                } else {
                    Picasso.with(context).load(R.drawable.mascot_small).transform(new BlurTransform(context)).into(headerBackgroundImage);
                }
                headerStationName.setText(Radio.getRadioStationListOriginal().get(0).getName());

            }
        }).execute();


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed);
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
        }



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                drawerLayout.closeDrawer(Gravity.START);

                switch (id) {

                    case R.id.navigation_settings:
                        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                            getSupportFragmentManager().popBackStackImmediate();
                        }

                        if (getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG) == null) {


                            final SettingsFragment settingsFragment = new SettingsFragment();
                            settingsFragment.setSettingsListener(new SettingsFragment.SettingsListener() {
                                @Override
                                public void SettingsUpdate() {
                                    if(getSupportFragmentManager().findFragmentByTag(RadioStationSelectionFragment.TAG) != null) {
                                        ((RadioStationSelectionFragment) getSupportFragmentManager().findFragmentByTag(RadioStationSelectionFragment.TAG)).updateRecyclerView();
                                    }
                                }
                            });
                            getSupportFragmentManager().beginTransaction().add(R.id.main_container,
                                    settingsFragment, SettingsFragment.TAG).addToBackStack(null).commit();
                        }
                        break;

                    case R.id.navigation_add_stream:
                        if (getSupportFragmentManager().findFragmentByTag(AddRadioStationFragment.TAG) == null) {

                            AddRadioStationFragment addRadioStationFragment = new AddRadioStationFragment();
                            addRadioStationFragment.setAddRadioStationListener(new AddRadioStationFragment.AddRadioStationListener() {
                                @Override
                                public void stationUpdate() {
                                    if(getSupportFragmentManager().findFragmentByTag(RadioStationSelectionFragment.TAG) != null) {
                                        ((RadioStationSelectionFragment) getSupportFragmentManager().findFragmentByTag(RadioStationSelectionFragment.TAG)).updateRecyclerView();
                                    }
                                }
                            });

                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_up, R.anim.slide_down).add(R.id.main_overlay_container,
                                    addRadioStationFragment, AddRadioStationFragment.TAG).addToBackStack(null).commit();

                            transparentView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in));
                            transparentView.setVisibility(View.VISIBLE);
                        }

                        break;


                    case R.id.navigation_info:

                        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                            getSupportFragmentManager().popBackStackImmediate();
                        }

                        if (getSupportFragmentManager().findFragmentByTag(InfoFragment.TAG) == null) {
                            getSupportFragmentManager().beginTransaction().add(R.id.main_container,
                                    new InfoFragment(), InfoFragment.TAG).addToBackStack(null).commit();
                        }
                        break;

                }

                return false;
            }
        });

        RadioListener radioListener = new RadioListener() {
            @Override
            public void onRadioUpdate() {

                switch (Radio.getRadioState()) {

                    case PLAY:
                        new NotificationPlayer(context);
                        if (!Radio.getCurrentRadioStation().getImageURL().equals("")) {
                            Picasso.with(context).load(Radio.getCurrentRadioStation().getImageURL()).transform(new BlurTransform(context)).into(headerBackgroundImage);
                        } else {
                            Picasso.with(context).load(R.drawable.mascot_small).transform(new BlurTransform(context)).into(headerBackgroundImage);
                        }
                        headerStationName.setText(Radio.getCurrentRadioStation().getName());
                        break;

                    case STOP:
                        new NotificationPlayer(context);
                        break;

                    case LOADING:
                        if (playerIsHidden) {
                            playerContainer.setVisibility(View.VISIBLE);
                            playerIsHidden = false;
                        }
                        new NotificationPlayer(context);
                        break;

                    case BUFFERING:
                        new NotificationPlayer(context);
                        break;

                    case HIDDEN:
                        if (playerIsHidden) {
                            playerContainer.setVisibility(View.GONE);
                            playerIsHidden = true;

                        }
                        break;
                }
            }
        };


        Radio.addRadioListener(radioListener);

    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();

        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Snackbar.make(findViewById(R.id.main_drawer_layout),
                    getResources().getString(R.string.close_app), Snackbar.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            System.exit(0);
        }
    }

}
