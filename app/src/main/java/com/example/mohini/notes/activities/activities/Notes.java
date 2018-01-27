
package com.example.mohini.notes.activities.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mohini.notes.R;
import com.example.mohini.notes.activities.adapter.NoteAdapter;
import com.example.mohini.notes.activities.adapter.TagAdapter;
import com.example.mohini.notes.activities.fragment.AddNoteFragment;
import com.example.mohini.notes.activities.interfaces.ApiInterface;
import com.example.mohini.notes.activities.model.DataModel;
import com.example.mohini.notes.activities.model.TagModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class Notes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView userpic;
    private TextView userEmail, userName;
    Bundle bundle = new Bundle();
    public android.support.v4.app.FragmentManager manager = getSupportFragmentManager();    //Initializing Fragment Manager.
    public  AddNoteFragment Fragment = new AddNoteFragment();
    public  android.support.v4.app.FragmentTransaction transaction;
    public static RecyclerView recyclerView,tagRecyclerView;
    public static NoteAdapter adapter;
    public static int i = 0;
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static ArrayList<DataModel> arraydata=new ArrayList<>();
    public static ArrayList<String> tagData=new ArrayList<>();
    public static DatabaseReference rootreference,titlerefrence,noterefrence;
    public static String userId;
    public static ArrayList<String> keyStore=new ArrayList<>();
    public static FloatingActionButton fab;
    Retrofit retrofit;
    OkHttpClient defaultHttpClient;
    ArrayList<DataModel> notes = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tagRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
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
                fab.setVisibility(View.INVISIBLE);
                getSupportActionBar().setTitle("Add Note");
                transaction = manager.beginTransaction();
                transaction.replace(R.id.recyclerViewXML, Fragment).commit();
                transaction.show(Fragment);
            }
        });



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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
            final DataModel noteData = new DataModel(email, "title", "note", "color", "tag");
            apiService.notes(noteData).enqueue(new Callback<List<DataModel>>() {
                //        apiService.savePost(username, password, phone).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<List<DataModel>> call, Response<List<DataModel>> response) {
                    if (response.isSuccessful()) {
                        List<DataModel> dataModelList = response.body();
                        for (DataModel dataModel : dataModelList) {
                            notes.add(dataModel);
                        }

                        StaggeredGridLayoutManager staggeredGridLayoutManager;
                        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                                StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setHasFixedSize(true);   //If the RecyclerView knows in advance that its size doesn't depend on the adapter content, then it will skip checking if its size should change every time an item is added or removed from the adapter.
                        recyclerView.setLayoutManager(staggeredGridLayoutManager);  //Displays recycler view in fragment.

                        adapter = new NoteAdapter(Notes.this, notes);
                       //registerForContextMenu(recyclerView);
                        recyclerView.setAdapter(adapter);

                        Toast.makeText(getApplicationContext(), "Notes..!! ", Toast.LENGTH_SHORT).show();

                    } else if (response.code() == 500) {
                        Toast.makeText(getApplicationContext(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getApplicationContext(), "wrong..", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<List<DataModel>> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getApplicationContext(), "failed ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            onStart();
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
            LoginActivity obj=new LoginActivity();
            obj.signOut();
            Intent intent=new Intent(Notes.this,LoginActivity.class);

            Toast.makeText(Notes.this, "You have successfully logged Out", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } else if (id == R.id.Home) {
            fab.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Home");
            onStart();

        } else if (id == R.id.ImporrtantNotes) {
            fab.setVisibility(View.INVISIBLE);
            getSupportActionBar().setTitle("Important Notes");
            //prioirtyNotes("Important");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
