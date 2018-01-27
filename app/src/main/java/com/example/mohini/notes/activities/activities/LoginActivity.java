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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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
    public static String name, email, pic,serverToken;
    public DatabaseReference databaseUser;
    public GoogleSignInAccount acct;
    public static SharedPreferences pref;
    public static Boolean login = false;

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

        //checks if user is already loggedin and perform suitable action.
        try {
            pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            if (pref.getBoolean("login", false) == true) {
                signIn();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            mProgressDialog = ProgressDialog.show(this, "Signing in  " + email, "Signing in...");
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            //Storing  basic information of logged in account in variables to send in another activity
            name = acct.getDisplayName();
            email = acct.getEmail();
            pic = acct.getPhotoUrl().toString();
            String token= acct.getIdToken();
            //putting data in SharedPreferences.
            pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            login = true;
            editor.putBoolean("login", login);
            editor.commit();



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
                LoginUserDetails user = new LoginUserDetails(email,token);
                user.setEmail(email);
                user.setToken(token);
                apiService.loginUser(user).enqueue(new Callback<LoginUserDetails>() {
                    @Override
                    public void onResponse(Call<LoginUserDetails> call, Response<LoginUserDetails> response) {
                        mProgressDialog.dismiss();
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
                            LoginUserDetails user =response.body();
                            serverToken=user.getToken();
                            Log.i("token : ",user.getToken());
                            Toast.makeText(getApplicationContext(), "Login Successful..!! ", Toast.LENGTH_SHORT).show();
                            Intent main = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(main);
                            finish();
                        } else if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "Login Successful.. ", Toast.LENGTH_SHORT).show();
                            Intent main = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(main);
                            finish();
                        } else if (response.code() == 500) {
                            Toast.makeText(getApplicationContext(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(getApplicationContext(), "Wrong Email or Password..", Toast.LENGTH_SHORT).show();
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

 //         Toast.makeText(getApplicationContext(), "You have successfully logged in", Toast.LENGTH_SHORT).show();
            //sending intent to another class
//            Intent intent = new Intent(LoginActivity.this, Notes.class);
//            mProgressDialog = ProgressDialog.show(this, "Signing in  " + email, "Signing in...");
//            addUser();
//            startActivity(intent);

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
