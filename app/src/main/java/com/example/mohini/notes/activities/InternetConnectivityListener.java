package com.example.mohini.notes.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.mohini.notes.activities.activities.Notes;

import android.os.Handler;

import static com.example.mohini.notes.activities.activities.LoginActivity.loggedIn;

/**
 * Created by mohini on 5/2/18.
 */

public class InternetConnectivityListener extends BroadcastReceiver {
    public InternetConnectivityListener() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (loggedIn == 1)
            checkInternetConnection(context);
    }

    private void checkInternetConnection(final Context context) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (Notes.isInternetAvailable(context) == false) {
                    // retry once after 30 seconds
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Notes.isInternetAvailable(context) == false) {
                                Toast.makeText(context, "Internet Not available", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Internet available", Toast.LENGTH_SHORT).show();


                            }
                        }
                    }, 10 * 1000);
                } else {
                    Toast.makeText(context, "Internet available", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }
}
