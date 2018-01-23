package com.example.mohini.notes.activities.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mohini.notes.R;
import com.example.mohini.notes.activities.model.LoginUserDetails;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private Button loginButton;
    private static GoogleApiClient mGoogleApiClient;
    public static GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog mProgressDialog;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    public static String name, email, pic;
    public DatabaseReference databaseUser;
    //public static final String SCOPES = "https://www.googleapis.com/auth/plus.login "
            //+ "https://www.googleapis.com/auth/drive.file";
    public static final String SCOPES="504518996242-jhiilc56r9a02kde8k1q510j88p9f3be.apps.googleusercontent.com";
    public GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Finding button from xml through id
        loginButton = (Button) findViewById(R.id.gmailsingin);

        //Listening from the button
        loginButton.setOnClickListener(this);

        //Configure signin to request the user for basic profile info,
        //basic info is stored in  DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("504518996242-jhiilc56r9a02kde8k1q510j88p9f3be.apps.googleusercontent.com")
                .build();

        //Build the GoogleSignInClient with the options provided by googleSignInOptions
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    //Function for checking internet connection
    public boolean check_InternnetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            return connected;
        }
        return connected;
    }

    //Function for signin with google account
    private void signIn() {
        check_InternnetConnection();
        if (connected) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            Toast.makeText(this, "Check your internet connection please", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            //Storing  basic information of logged in account in variables to send in another activity
            name = acct.getDisplayName();
            email = acct.getEmail();
            pic = acct.getPhotoUrl().toString();
            String token= acct.getIdToken();
            Toast.makeText(getApplicationContext(), "You have successfully logged in", Toast.LENGTH_SHORT).show();
            //sending intent to another class
            Intent intent = new Intent(LoginActivity.this, Notes.class);
            mProgressDialog = ProgressDialog.show(this, "Signing in  " + email, "Signing in...");
            addUser();
            startActivity(intent);
        }
    }

    //Function for signOut with google account
    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connectin failed", Toast.LENGTH_SHORT).show();
    }

    boolean connected = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.gmailsingin:
                signIn();
                break;
        }

    }

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static String userId;

    //adds note in the Firebase realtime database.
    public void addUser() {
        databaseUser = database.getInstance().getReference("LoggedIn User");
        userId = databaseUser.push().getKey();
        LoginUserDetails user = new LoginUserDetails(userId, name, email);
        databaseUser.child(userId).setValue(user);


    }




}
