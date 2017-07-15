package com.example.rashmitiwari.we_sync;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
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

public class SubActivity extends YouTubeBaseActivity {

    private static final int RECOVERY_REQUEST = 1;
    private static final String TAG = "MainActivity";

    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private YouTubePlayer mplayer;

    private YouTubePlayerView youTubePlayerView;
    Button mJoinLeaveRoom;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    ImageButton next_page;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth auth;
    private DatabaseReference mMessageDatabaseReference, mDatabaseRef;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotoStorageReference;
    EditText seekToText, mRoomId;


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

        seekToText = (EditText) findViewById(R.id.seek_to_text);
        mRoomId=(EditText) findViewById(R.id.room_id);
        mJoinLeaveRoom= (Button) findViewById(R.id.join_leave);

        onInitializedListener= new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean b) {
                mplayer=player;
                mplayer.setPlayerStateChangeListener(playerStateChangeListener);
                mplayer.setPlaybackEventListener(playbackEventListener);
                mplayer.loadVideo("a18py61_F_w");
                mplayer.pause();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        Button seekToButton = (Button) findViewById(R.id.seek_to_button);
        seekToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekVideo(Integer.valueOf(seekToText.getText().toString())*1000);
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


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(config.API_KEY, onInitializedListener);
            syncVideoState();

        }
    }

    public  void syncVideoState(){
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        while (mplayer.isPlaying()){
            int skipTime= mplayer.getCurrentTimeMillis();
            seekVideo(skipTime);
            Log.d(TAG, "time = "+skipTime);
            Toast.makeText(this,"seeking", Toast.LENGTH_SHORT).show();
        }

    }

    public void seekVideo(int skipSec){
        final SyncBoard seekTime = new SyncBoard(skipSec);
        mMessageDatabaseReference.setValue(seekTime);
        mMessageDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long seekSec= dataSnapshot.child("skipTime").getValue(Long.class);
                Log.d(TAG, "time = "+seekSec);
                mplayer.seekToMillis((int) (seekSec * 1));
                mplayer.play();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
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
            showMessage("Playing");


        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            showMessage("Paused");
            int currentTime= mplayer.getCurrentTimeMillis();
           // seekVideo(currentTime);
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
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

    private void detachDatabaseReadListner() {
        if (mChildEventListener!=null) {
            mMessageDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener= null;
        }
    }

    public  void attachDatabaseReadListner(){
        if (mChildEventListener== null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    SyncBoard syncBoard = dataSnapshot.getValue(SyncBoard.class);
                    //SyncBoard synchBoard= new SyncBoard();
                    int seekTime= syncBoard.getSkipTime();
                    mplayer.seekToMillis((seekTime * 1000));
                    mplayer.play();
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessageDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    public void leaveOrJoinRoom(EditText mRoomId, Button mJoinLeaveRoom) throws InterruptedException {
        final String room_id = this.mRoomId.getText().toString();
        String ReadyValue= "no";
        final String user_id = auth.getCurrentUser().getUid().toString();
        if(this.mJoinLeaveRoom.getText().toString().equals("Join Room")){
            Toast.makeText(getApplicationContext(), "join room condition worked", Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(room_id)){
                Toast.makeText(getApplicationContext(), "Enter room id!", Toast.LENGTH_SHORT).show();
                return;
            }
            else {

                final DatabaseReference current_user_db= mMessageDatabaseReference.child(room_id);
                final ReadyState readyState= new ReadyState(ReadyValue);
                current_user_db.child(user_id).setValue(readyState);
                mDatabaseRef.child("Users").child(user_id).child("roomId").setValue(room_id);
                this.mJoinLeaveRoom.setText("Leave Room");
                isEveryMemberReady();

//                current_user_db.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        ReadyState ready = dataSnapshot.child(user_id).getValue(ReadyState.class);
//                        //readyState.setReady(dataSnapshot.child(user_id).getValue(ReadyState.class).getReady());
//                        Toast.makeText(getApplicationContext(), ready.getReady(), Toast.LENGTH_SHORT).show();
//                        //Log.d(TAG,"state"+ ready.getReady());
//                        if (ready.getReady().equals("no")){
//                            try {
//                                Thread.sleep(5000);
//                                alertDialog(room_id,user_id, current_user_db);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "Failed to read value.", databaseError.toException());
//                    }
//                });
            }
        }
        else if(this.mJoinLeaveRoom.getText().toString().equals("Leave Room")){

            if (TextUtils.isEmpty(room_id)){
                Toast.makeText(getApplicationContext(), "Enter room id!", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                Toast.makeText(getApplicationContext(), "leave room condition worked", Toast.LENGTH_SHORT).show();
                DatabaseReference current_user_db = mMessageDatabaseReference.child(room_id);
               // mDatabaseRef.child("Users").child(user_id).child("roomId").setValue("");
                current_user_db.child(user_id).removeValue();
                this.mJoinLeaveRoom.setText("Join Room");
            }

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
                Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                current_user_db.child(user_id).child("ready").setValue("yes");

            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                current_user_db.child(user_id).child("ready").setValue(" ");
                current_user_db.child(user_id).child("ready").setValue("no");
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    public void isEveryMemberReady(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_id= auth.getCurrentUser().getUid();
                UserInformation userInformation= dataSnapshot.child("Users").child(user_id).getValue(UserInformation.class);
                String room_id= userInformation.getRoomId();
                int dbCount = (int) dataSnapshot.child("Room Id").child(room_id).getChildrenCount();
               Toast.makeText(getApplicationContext(), room_id + "  " + dbCount, Toast.LENGTH_SHORT).show();
                int count= 0;
                boolean returnValue;
                for (DataSnapshot ds : dataSnapshot.child("Room Id").child(room_id).getChildren()){
                    ReadyState ready = ds.getValue(ReadyState.class);
                    String readyState=  ready.getReady();
                    if (readyState.equals("yes")) {
                        count = count + 1;
                        Toast.makeText(getApplicationContext(), ready.getReady() + "  "+count, Toast.LENGTH_SHORT).show();
                    }

                }
                Toast.makeText(getApplicationContext(),  "  "+count, Toast.LENGTH_SHORT).show();
                if (count==dbCount){
                    try {
                        Thread.sleep(5000);
                        mplayer.play();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    mplayer.pause();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



}
