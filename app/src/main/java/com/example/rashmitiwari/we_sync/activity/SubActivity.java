package com.example.rashmitiwari.we_sync.activity;

/*
  I m having problem with play button. play button (play_video) works perfectly if "current_user_db.child("time").setValue(0);" this
  part of code is removed from leaveOrJoin() method. else ready state of Room Id keeps changing to "no" only. this value is never
  set to "yes" and because of this video is not playing.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rashmitiwari.we_sync.Adapter.UserListAdapter;
import com.example.rashmitiwari.we_sync.R;
import com.example.rashmitiwari.we_sync.model.Information;
import com.example.rashmitiwari.we_sync.model.ReadyState;
import com.example.rashmitiwari.we_sync.model.SyncBoard;
import com.example.rashmitiwari.we_sync.model.UserInformation;
import com.example.rashmitiwari.we_sync.model.config;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.rashmitiwari.we_sync.R.id.roomId;

/**
 *
 */
public class SubActivity extends YouTubeBaseActivity {
    private static final int RECOVERY_REQUEST = 1;
    private static final String TAG = "MainActivity";

    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private YouTubePlayer mplayer;
    private Switch doNotDisturnBtn;

    private YouTubePlayerView youTubePlayerView;
    ImageButton mJoinLeaveRoom, videoSearch;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    ImageButton next_page, play_video, pause_video, seekLeftBtn, seekRightBtn;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth auth;
    private DatabaseReference mMessageDatabaseReference, mDatabaseRef;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotoStorageReference;
    EditText seekToText, mRoomId, mVideoId;
    int databaseTime ;
    TextView textView;

    private DatabaseReference userlistReference;
    private ValueEventListener mUserListListener;
    ArrayList<String> usernamelist = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    ListView userListView;
    UserListAdapter mUserListAdapter;

    String videoId, room_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Toolbar toolbar= (Toolbar) findViewById(R.id.app_bar);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mMessageDatabaseReference= mFirebaseDatabase.getReference().child("Room Id");
        mDatabaseRef= mFirebaseDatabase.getReference();

        youTubePlayerView= (YouTubePlayerView) findViewById(R.id.youtube_player_view);
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        textView= (TextView) findViewById(R.id.text) ;

        userListView = (ListView) findViewById(R.id.memberListView);
        List<Information> informations = new ArrayList<>();
        mUserListAdapter = new UserListAdapter(this, R.layout.item_user_list, informations);
        userListView.setAdapter(mUserListAdapter);

        doNotDisturnBtn= (Switch) findViewById(R.id.do_not_disturb);
        mRoomId=(EditText) findViewById(R.id.roomId);
        mVideoId= (EditText) findViewById(R.id.videoId);
        mJoinLeaveRoom= (ImageButton) findViewById(R.id.join_leave);
        videoSearch=(ImageButton) findViewById(R.id.go);
        pause_video= (ImageButton)findViewById(R.id.pauseVideo);
        play_video= (ImageButton) findViewById(R.id.playVideo);
        seekLeftBtn= (ImageButton) findViewById(R.id.seekLeftVideo);
        seekRightBtn= (ImageButton) findViewById(R.id.seekRightVideo);
        videoId= "a18py61_F_w";

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_id= auth.getCurrentUser().getUid();
                UserInformation userInformation= new UserInformation();
                userInformation.setRoomId(dataSnapshot.child("Users").child(user_id).getValue(UserInformation.class).getRoomId());
                room_id= userInformation.getRoomId();
                databaseTime = dataSnapshot.child("Room Id").child(room_id).child("time").getValue(int.class);
                Toast.makeText(getApplicationContext(), databaseTime + "  " , Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        onInitializedListener= new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean b) {
                mplayer=player;
                mplayer.setPlayerStateChangeListener(playerStateChangeListener);
                mplayer.setPlaybackEventListener(playbackEventListener);
                mDatabaseRef.child("Video").child(videoId).child("video Id").setValue(videoId);
                mDatabaseRef.child("Video").child(videoId).child("room Id").setValue(room_id);
                mplayer.cueVideo(videoId);
                mplayer.play();
                //isEveryMemberReady();
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        seekRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekVideo(databaseTime+10000);
            }
        });

        seekLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekVideo(databaseTime-10000);
            }
        });


        youTubePlayerView.initialize(config.API_KEY, onInitializedListener);

        mJoinLeaveRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    leaveOrJoinRoom(mRoomId, mJoinLeaveRoom);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });
        /**
         *
         */
        pause_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int currentTime= mplayer.getCurrentTimeMillis();
                mplayer.pause();
                final String user_id = auth.getCurrentUser().getUid().toString();

                mDatabaseRef.child("Video").child(videoId).child("pausedUser").setValue(user_id);
            }
        });

        play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("Room Id").child(room_id).child("ready").setValue("yes");
                mplayer.play();
            }
        });

        doNotDisturnBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mMessageDatabaseReference.child(room_id).child("Do not disturb").setValue(true);

                }
                else{
                    mMessageDatabaseReference.child(room_id).child("Do not disturb").setValue(false);
                }
                mMessageDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean switchValue= dataSnapshot.child(room_id).child("Do not disturb").getValue(boolean.class);
                        doNotDisturnBtn.setChecked(switchValue);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayRoomMembers();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(config.API_KEY, onInitializedListener);

        }
    }

    public void seekVideo(int skipSec){
        mDatabaseRef.child("Room Id").child(room_id).child("time").setValue(skipSec);
        mplayer.pause();

    }

    protected Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            //mDatabaseRef.child("Room Id").child(room_id).child("ready").setValue("yes");
            isEveryMemberReady();
            showMessage("Playing");
        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
            //mDatabaseRef.child("Video").child(videoId).child("playerStste").setValue("buffering");
            // mplayer.pause();
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
            final int currentTime= mplayer.getCurrentTimeMillis();
            mplayer.pause();
            final String user_id = auth.getCurrentUser().getUid().toString();
            //if(!room_id.equals(""))
            mDatabaseRef.child("Room Id").child(room_id).child("time").setValue(currentTime);

            //mDatabaseRef.child("Room Id").child(room_id).child("ready").setValue("no");



        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }

    /*
     code to join room or leave room. Room id is entered by user and then ready state, time, user id is updated in that room
     */

    public void leaveOrJoinRoom(final EditText mRoomId, final ImageButton mJoinLeaveRoom) throws InterruptedException {
        final String room_id = this.mRoomId.getText().toString();
        String ReadyValue= "no";
        final String user_id = auth.getCurrentUser().getUid().toString();
        if(this.mJoinLeaveRoom.getTag().toString().equals("join")){
            Toast.makeText(getApplicationContext(), "join room condition worked", Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(room_id)){
                Toast.makeText(getApplicationContext(), "Enter room id!", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                final DatabaseReference current_user_db= mMessageDatabaseReference.child(room_id);
                final ReadyState readyState= new ReadyState(ReadyValue);
                current_user_db.child("ready").setValue("no");
                current_user_db.child("user").child(user_id).setValue(readyState);
                mDatabaseRef.child("Users").child(user_id).child("roomId").setValue(room_id);
                mMessageDatabaseReference.child(room_id).child("time").setValue(0);
                mRoomId.setText("");
                mRoomId.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                mJoinLeaveRoom.setTag("leave");
                mJoinLeaveRoom.setImageResource(R.mipmap.leave);
                textView.setText("you have joined room "+room_id+"  ");

            }
        }
        else if(this.mJoinLeaveRoom.getTag().toString().equals("leave")){

                Toast.makeText(getApplicationContext(), "leave room condition worked", Toast.LENGTH_SHORT).show();
                DatabaseReference current_user_db = mMessageDatabaseReference.child(room_id);
                // mDatabaseRef.child("Users").child(user_id).child("roomId").setValue("");
                current_user_db.child("user").child(user_id).removeValue();
                textView.setText("");
                textView.setVisibility(View.GONE);
                mRoomId.setVisibility(View.VISIBLE);
                this.mJoinLeaveRoom.setTag("join");
            this.mJoinLeaveRoom.setImageResource(R.mipmap.join);


        }
    }


    private void alertDialog(String room_id, final String user_id, final DatabaseReference current_user_db) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Ready...");
        // Setting Dialog Message
        alertDialog.setMessage("Are you ready to watch video?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                // Write your code here to invoke YES event
                current_user_db.child("user").child(user_id).child("ready").setValue("yes");

            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                current_user_db.child("user").child(user_id).child("ready").setValue(" ");
                current_user_db.child("user").child(user_id).child("ready").setValue("no");
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    /*
    This method checks the ready value of room. if ready valur is no it pauses the video
    else plays  video.
     */
    public void isEveryMemberReady(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListner();
    }
/*
   Method to display list of members in room
 */
    public void displayRoomMembers(){
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.child("Room Id").child(room_id).child("user").getChildren()) {
                    // ReadyState ready = ds.getValue(ReadyState.class);
                    // String readyState = ready.getReady();
                    String user= ds.getKey();
                    Information information = dataSnapshot.child("Users").child(user).getValue(Information.class);
                    mUserListAdapter.add(information);
                    Toast.makeText(getApplicationContext(), "  "+information.getUserName(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: "+databaseError.getMessage() );
            }

        });
    }

    private void detachDatabaseReadListner() {
        if (mChildEventListener!=null) {
            userlistReference.removeEventListener(mChildEventListener);
            mChildEventListener= null;
        }
    }


}
