package com.example.rashmitiwari.we_sync.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//import com.example.rashmitiwari.we_sync.ui.acrivity.UserListingActivity;
import com.example.rashmitiwari.we_sync.Adapter.MessageAdapter;
import com.example.rashmitiwari.we_sync.Adapter.UserListAdapter;
import com.example.rashmitiwari.we_sync.R;
import com.example.rashmitiwari.we_sync.model.Information;
import com.example.rashmitiwari.we_sync.model.Message;
import com.example.rashmitiwari.we_sync.model.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    String mUsername, user_id;
    private Toolbar toolbar;
    private ViewPager mPager;
    private Setting setting;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private static final String TAG = "UserList" ;
    private DatabaseReference userlistReference;
    private ValueEventListener mUserListListener;
    ArrayList<String> usernamelist = new ArrayList<>();
    ArrayAdapter arrayAdapter;;
    private ChildEventListener mChildEventListener;
    ListView userListView;
    UserListAdapter mUserListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userlistReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userListView = (ListView) findViewById(R.id.userListView);
        List<Information> informations = new ArrayList<>();
        mUserListAdapter = new UserListAdapter(this, R.layout.item_user_list, informations);
        userListView.setAdapter(mUserListAdapter);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User auth state is changed - User is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
                else{
                    attachDatabaseReadListner();
                }
            }
        };

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
            }
        });



    }
    private void onSignedInInitialize() {
        attachDatabaseReadListner();

    }

    public void onStart() {
        super.onStart();
       auth.addAuthStateListener(authListener);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Setting.class));
        }
        if (id == R.id.action_touch_intercept_activity) {
            startActivity(new Intent(this, SubActivity.class));

        }
        if (id == R.id.sketch) {
            startActivity(new Intent(this, SketchPad.class));
        }
        if (id == R.id.sign_out) {
          //  startActivity(new Intent(this, SubActivity.class));
           // setting.signOut();
            FirebaseAuth.getInstance().signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onPause() {
        super.onPause();
        if(authListener!=null) {
            auth.removeAuthStateListener(authListener);
        }
        detachDatabaseReadListner();
        mUserListAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authListener);
    }

    private void detachDatabaseReadListner() {
        if (mChildEventListener!=null) {
            userlistReference.removeEventListener(mChildEventListener);
            mChildEventListener= null;
        }
    }

    public  void attachDatabaseReadListner(){

        if (mChildEventListener== null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                   // for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String userId= auth.getCurrentUser().getUid();
                        Information information = dataSnapshot.getValue(Information.class);
                    String username = dataSnapshot.child(userId).child("name").getValue(String.class);
                    if (!information.getUserName().equals(username)){
                        mUserListAdapter.add(information);

                    }

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
            userlistReference.addChildEventListener(mChildEventListener);
        }
    }

}

