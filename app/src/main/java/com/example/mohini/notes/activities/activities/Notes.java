
package com.example.mohini.notes.activities.activities;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mohini.notes.R;
import com.example.mohini.notes.activities.adapter.NoteAdapter;
import com.example.mohini.notes.activities.adapter.TagAdapter;
import com.example.mohini.notes.activities.fragment.AddNoteFragment;
import com.example.mohini.notes.activities.interfaces.ApiInterface;
import com.example.mohini.notes.activities.model.LoginUserDetails;
import com.example.mohini.notes.activities.model.NoteModel;
import com.example.mohini.notes.activities.model.SharedNotes;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.mohini.notes.activities.activities.LoginActivity.email;
import static com.example.mohini.notes.activities.activities.LoginActivity.serverToken;
import static com.example.mohini.notes.activities.adapter.NoteAdapter.editNoteid;
import static com.example.mohini.notes.activities.adapter.NoteAdapter.pinned;

public class Notes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //declaring control from xml
    private ImageView userpic;
    private TextView userEmail, userName;
    Bundle bundle = new Bundle();
    public android.support.v4.app.FragmentManager manager = getSupportFragmentManager();    //Initializing Fragment Manager.
    public  AddNoteFragment Fragment = new AddNoteFragment();
    public  android.support.v4.app.FragmentTransaction transaction;
    public static RecyclerView recyclerView,tagRecyclerView,pinrecyclerView;
    public static NoteAdapter adapter;
    public static TagAdapter tagAdapter;
    public static FloatingActionButton fab;
    Retrofit retrofit;
    OkHttpClient defaultHttpClient;
    public static ArrayList<NoteModel> notes = new ArrayList<>();
    public static ArrayList<String> tagList = new ArrayList<>();
    public static SharedPreferences pref;
    public static boolean login = false;
    public static int isShared = 0;
    public static TextView pinnedNote,pinnedTitle, pinnedTag;
    public static CardView pinnedNoteLayout;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //finding xml controls with id
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tagRecyclerView=(RecyclerView)findViewById(R.id.tagrecyclerView);
        pinnedNote = (TextView) findViewById(R.id.pinnedNote);
        pinnedTitle = (TextView) findViewById(R.id.pinnedtitle);
        pinnedTag = (TextView) findViewById(R.id.pinnedTag);
        pinnedNoteLayout = (CardView) findViewById(R.id.pinnedNoteLayout);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#339988")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.add, Notes.this.getTheme()));
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.add));
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pinnedNoteLayout.getLayoutParams();
                lp.height = 0;
                pinnedNoteLayout.setLayoutParams(lp);
                fab.setVisibility(View.INVISIBLE);
                getSupportActionBar().setTitle("Add Note");
                transaction = manager.beginTransaction();
                transaction.replace(R.id.recyclerViewXML, Fragment).commit();
                transaction.show(Fragment);
            }
        });

        //registers pinnedlayout for context menu.
        registerForContextMenu(pinnedNoteLayout);

        //checks if any not if already pinned to top or not
        try {
            pinned = getApplicationContext().getSharedPreferences("pinned", 0);
            if (pinned.getBoolean("isPinned", false) == true) {
                setPinnedNote(pinned.getString("pinnedNotetitle", "ads"), pinned.getString("pinnedNote", "as"), pinned.getString("pinnedNoteTag", "asd"), pinned.getString("pinnedNoteColor", "asd"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bundle = getIntent().getExtras();
        View header = navigationView.getHeaderView(0);
        userpic = (ImageView) header.findViewById(R.id.userPic);
        userEmail = (TextView) header.findViewById(R.id.userEmail);
        userName = (TextView) header.findViewById(R.id.userName);
        userName.setText(LoginActivity.name);
        userEmail.setText(email);
        Glide.with(getApplicationContext()).load(LoginActivity.pic)
                .thumbnail(0.5f)
                .crossFade()
                .transform(new CircularTranformation(Notes.this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userpic);
        showNotes();
        if(!(editNoteid==null))
        {
            fab.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Add Note");
            transaction = manager.beginTransaction();
            transaction.replace(R.id.recyclerViewXML, Fragment).commit();
            transaction.show(Fragment);
        }


    }
    //sets data to the top layout for pinned Note.
    void setPinnedNote(String title, String note, String tag, String color) {
        pinnedNote.setText(note);
        pinnedTag.setText(tag);
        pinnedTitle.setText(title);
        if (color != null)
            pinnedNoteLayout.setBackgroundColor(Color.parseColor(color));
        ViewGroup.LayoutParams params = pinnedNoteLayout.getLayoutParams();
        params.height = 300;
        pinnedNoteLayout.setContentPadding(0,5,0,0);
    }
    public void showNotes()
    {
        //HttpCLient to Add Authorization Header.
        defaultHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .addHeader("authorization", "bearer " + serverToken).build();
                                return chain.proceed(request);
                            }
                        }).build();
        //Retrofit to retrieve JSON data from server.
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(defaultHttpClient)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            final NoteModel noteData = new NoteModel(editNoteid,email, "title", "note", "color", "tag");
            apiService.allnotes(noteData).enqueue(new Callback<List<NoteModel>>() {
                @Override
                public void onResponse(Call<List<NoteModel>> call, Response<List<NoteModel>> response) {
                    if (response.isSuccessful()) {
                        notes.clear();
                        tagList.clear();
                        List<NoteModel> noteModelList = response.body();
                        for (NoteModel noteModel : noteModelList) {
                            notes.add(noteModel);
                            if(!noteModel.getTag().isEmpty())
                            {
                                if(!tagList.contains(noteModel.getTag()))
                                {
                                    tagList.add(noteModel.getTag());

                                }
                            }
                        }

        StaggeredGridLayoutManager tagStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
        StaggeredGridLayoutManager.VERTICAL);

        StaggeredGridLayoutManager tagStaggeredGridLayoutManager1 = new StaggeredGridLayoutManager(1,
        StaggeredGridLayoutManager.HORIZONTAL);

        tagRecyclerView.setLayoutManager(tagStaggeredGridLayoutManager1);
        tagRecyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(tagStaggeredGridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new NoteAdapter(Notes.this, notes);
        tagAdapter = new TagAdapter(Notes.this, tagList);
        recyclerView.setAdapter(adapter);
        tagRecyclerView.setAdapter(tagAdapter);
        } else if (response.code() == 500) {
         Toast.makeText(getApplicationContext(), R.string.ServerError, Toast.LENGTH_SHORT).show();
         } else if (response.code() == 404) {
         Toast.makeText(getApplicationContext(), R.string.RequestNotFound, Toast.LENGTH_SHORT).show();
          }

           }

                @Override
                public void onFailure(Call<List<NoteModel>> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to get Notes ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //creates contextmenu and add options.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Options :");
        menu.add(0, v.getId(), 0, "Unpin from top");
        menu.add(0, v.getId(), 0, "Cancel");
    }
    //In case any option is selected from Context menu.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //unpins the note from top.
        if (item.getTitle()=="Unpin from top") {
            pinned = this.getSharedPreferences("pinned", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pinned.edit();
            editor.putBoolean("isPinned", false);
            editor.commit();
            ViewGroup.LayoutParams params = pinnedNoteLayout.getLayoutParams();
            params.height = 0;
            pinnedNoteLayout.setLayoutParams(params);
        }
        else if(item.getTitle().equals(R.string.cancel))
        {
            return false;
        }

        return true;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (Fragment != null) {
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(Fragment);
                transaction.commit();
            }
            try {
                pinned = getApplicationContext().getSharedPreferences("pinned", 0);
                if (pinned.getBoolean("isPinned", false) == true) {
                    setPinnedNote(pinned.getString("pinnedNotetitle", "ads"), pinned.getString("pinnedNote", "as"), pinned.getString("pinnedNoteTag", "asd"), pinned.getString("pinnedNoteColor", "asd"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            getSupportActionBar().setTitle("My Notes");
            fab.setVisibility(View.VISIBLE);
            isShared = 0;
            showNotes();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout) {
            isShared = 0;
            showLogoutDialog();

        }
        else if (id == R.id.Home) {
            try {
                pinned = getApplicationContext().getSharedPreferences("pinned", 0);
                if (pinned.getBoolean("isPinned", false) == true) {
                    setPinnedNote(pinned.getString("pinnedNotetitle", "ads"), pinned.getString("pinnedNote", "as"), pinned.getString("pinnedNoteTag", "asd"), pinned.getString("pinnedNoteColor", "asd"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isShared=0;
            fab.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("My Notes");
            showNotes();

        }
        else if (id == R.id.sharedNotes) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pinnedNoteLayout.getLayoutParams();
            lp.height = 0;
            pinnedNoteLayout.setLayoutParams(lp);
            fab.setVisibility(View.INVISIBLE);
            getSupportActionBar().setTitle("Shared Notes");
            isShared = 1;
            showSharedNotes(email);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //shows notes shared with User.
    public void showSharedNotes(String email) {

        //HttpCLient to Add Authorization Header.
        defaultHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .addHeader("authorization", "bearer " + serverToken).build();
                                return chain.proceed(request);
                            }
                        }).build();
        //Retrofit to retrieve JSON data from server.
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(defaultHttpClient)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();

        ApiInterface apiService = retrofit.create(ApiInterface.class);
        try {
            SharedNotes sharedNotes = new SharedNotes("", email, "", "note", "", "", "", "");
            apiService.sharedNote(sharedNotes).enqueue(new Callback<List<NoteModel>>() {
                @Override
                public void onResponse(Call<List<NoteModel>> call, Response<List<NoteModel>> response) {
                    if (response.isSuccessful()) {
                        Log.i("here:", "post submitted to API." + response.body().toString());
                        List<NoteModel> noteList = response.body();
                        tagList.clear();
                        notes.clear();
                        for (NoteModel n : noteList) {
                            Log.i("note", n.getNote());
                            notes.add(n);
                            if (!n.getTag().equals(""))
                                tagList.add(n.getTag());
                        }

                        StaggeredGridLayoutManager staggeredGridLayoutManager;
                        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                                StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                        recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Displays recycler view in fragment.

                        adapter = new NoteAdapter(Notes.this, notes);
                        recyclerView.setAdapter(adapter);

                        StaggeredGridLayoutManager tagStaggeredGridLayoutManager;
                        tagStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,
                                StaggeredGridLayoutManager.HORIZONTAL);
                        tagRecyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                        tagRecyclerView.setLayoutManager(tagStaggeredGridLayoutManager);  //Displays recycler view in fragment.
                        TagAdapter tagAdapter = new TagAdapter(Notes.this, tagList);
                        tagRecyclerView.setAdapter(tagAdapter);


                    } else if (response.code() == 500) {
                        Toast.makeText(getApplicationContext(), "Some Error occured", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getApplicationContext(), "wrong..", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<List<NoteModel>> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("here", "Unable to submit post to API.");
                    Toast.makeText(getApplicationContext(), "failed ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
public void showLogoutDialog()
{
    @SuppressLint("RestrictedApi") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.myDialog));
    // set title
    alertDialogBuilder.setTitle("Logout");
    // set dialog message
    alertDialogBuilder
            .setMessage("Are you sure to logout?")
            .setCancelable(false)
            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    // if this button is clicked, close
                    // current activityLoginActivity obj=new LoginActivity();
                    LoginActivity obj=new LoginActivity();
                    obj.signOut();
                    //deleting login record from database.
                    SQLite.delete().from(LoginUserDetails.class).query();
                    LoginActivity.loggedIn = 0;
                    Intent intent=new Intent(Notes.this,LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(Notes.this, "You have successfully logged Out", Toast.LENGTH_SHORT).show();
                }
            });
    alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog,int id) {
            // if this button is clicked, just close
            // the dialog box and do nothing
            dialog.cancel();
        }
    });

    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();
    // show it
    alertDialog.show();


}
    //checks if internet is available or not.
    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            Log.e("SEVERE", "internet_check", e);
            return true;
        }
    }

}
