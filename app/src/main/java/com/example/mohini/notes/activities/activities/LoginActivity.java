package com.example.mohini.notes.activities.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.mohini.notes.activities.interfaces.ApiInterface;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private Button loginButton;
    private static GoogleApiClient mGoogleApiClient;
    public static GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog mProgressDialog;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    public static String name, email, pic,serverToken,token;
    public GoogleSignInAccount acct;
    public static SharedPreferences pref;
    public static boolean login = false;
    public static int loggedIn = 0;

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

//        //checks if user is already loggedin and perform suitable action.
//        try {
//            pref = getApplicationContext().getSharedPreferences("MyPref", 0);
//            if (pref.getBoolean("login", false) == true) {
//                signIn();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //checks database is user is already logged in or not.
        List<LoginUserDetails> user = SQLite.select().from(LoginUserDetails.class).queryList();
        if (user.size() != 0) {
            LoginUserDetails users = user.get(0);
            if (users.getEmail() != null)
                signIn();
        }

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

    private void handleSignInResult(GoogleSignInResult result){

        if(result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            //Storing  basic information of logged in account in variables to send in another activity
            name = acct.getDisplayName();
            email = acct.getEmail();
            pic = acct.getPhotoUrl().toString();
            token= acct.getId();

            mProgressDialog = ProgressDialog.show(this, "Signing in  " + email, "Signing in...");
            //putting data in SharedPreferences.
//            pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
//            SharedPreferences.Editor editor = pref.edit();
//            login = true;
//            editor.putBoolean("login", login);
//            editor.commit();


            //HttpCLient to Add Authorization Header.
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).build();

            //Retrofit to retrieve JSON data from server.
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiInterface.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                    .build();

            ApiInterface apiService = retrofit.create(ApiInterface.class);
            try {
                //initializing User constructor to send data to server.
                LoginUserDetails user = new LoginUserDetails(email, token);
                user.save();
                LoginUserDetails loggedinuser = new LoginUserDetails(email,token);
                //loggedinuser.setEmail(email);
                //loggedinuser.setToken(token);
                apiService.loginUser(loggedinuser).enqueue(new Callback<LoginUserDetails>() {
                    @Override
                    public void onResponse(Call<LoginUserDetails> call, Response<LoginUserDetails> response) {

                        if (response.isSuccessful()) {
                            mProgressDialog.dismiss();
                            LoginUserDetails user = response.body();
                            serverToken = user.getToken();
                            loggedIn = 1;
                            Toast.makeText(getApplicationContext(), R.string.LoginMessage, Toast.LENGTH_SHORT).show();
                            Intent main = new Intent(LoginActivity.this, Notes.class);
                            startActivity(main);
                            finish();

                        } else if (response.code() == 500) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.ServerError, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.RequestNotFound, Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onFailure(Call<LoginUserDetails> call, Throwable t) {
                        t.printStackTrace();
                        mProgressDialog.dismiss();
                        Log.e("here", "Unable to submit post to API.");
                        Toast.makeText(getApplicationContext(), "Login failed ", Toast.LENGTH_SHORT).show();

                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
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


}
