package com.example.rashmitiwari.we_sync.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//import com.example.rashmitiwari.we_sync.core.user.AddUserPresenter;
import com.example.rashmitiwari.we_sync.R;
import com.example.rashmitiwari.we_sync.model.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.widget.Toast.*;

public class SignUpActivity extends AppCompatActivity  {
    private EditText inputEmail, inputPassword, mUserName;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
   // private AddUserPresenter mAddUserPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        mUserName= (EditText) findViewById(R.id.userName);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressDialog= new ProgressDialog(this);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        //mAddUserPresenter = new AddUserPresenter(this);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            signupProcess();
            }
        });
    }

    protected void onResume() {
        super.onResume();
       // progressBar.setVisibility(View.GONE);
    }



    public void signupProcess(){
        final String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        final String name= mUserName.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            makeText(getApplicationContext(), "Enter email address!", LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            makeText(getApplicationContext(), "Enter password!", LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", LENGTH_SHORT).show();
            return;
        }
        if(!name.matches("[A-Za-z0-9]+")){
            mUserName.setError("only alphabet or number allowed");
        }


        progressDialog.setMessage("Sigining Up....");
        progressDialog.show();
        //create User
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the User. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in User can be handled in the listener.
                        if (!task.isSuccessful()) {
                            makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                    LENGTH_SHORT).show();
                        } else {
                            String user_id = auth.getCurrentUser().getUid();

                            DatabaseReference current_user_db= mDatabase.child(user_id);
                            UserInformation userInformation= new UserInformation(user_id,email, name, "" );
                            current_user_db.setValue(userInformation);
                            progressDialog.dismiss();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();

                        }
                        makeText(SignUpActivity.this,"you have been registered", LENGTH_SHORT).show();
                    }
                });
    }

}
