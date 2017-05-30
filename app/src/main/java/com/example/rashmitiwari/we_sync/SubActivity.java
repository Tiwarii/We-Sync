package com.example.rashmitiwari.we_sync;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class SubActivity extends YouTubeBaseActivity {

    YouTubePlayerView youTubePlayerView;
    ImageButton play_button;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    ImageButton next_page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Toolbar toolbar= (Toolbar) findViewById(R.id.app_bar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        play_button= (ImageButton) findViewById(R.id.play_btn);
        youTubePlayerView= (YouTubePlayerView) findViewById(R.id.youtube_player_view);
        next_page= (ImageButton) findViewById(R.id.nextpage);
        onInitializedListener= new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("15B3L78jWfI");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayerView.initialize(config.API_KEY,onInitializedListener);
            }
        });

        next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //Intent intent= new Intent(SubActivity.this,SketchPad.class) ;
                startActivity(new Intent(SubActivity.this, SketchPad.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        int id =item.getItemId();
        if (id== R.id.action_settings){
            return true;
        }
//        if (id == R.id.action_touch_intercept_activity){
//            startActivity(new Intent(this, SubActivity.class));
//        }
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
