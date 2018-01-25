
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
import com.example.mohini.notes.activities.model.DataModel;
import com.example.mohini.notes.activities.model.TagModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Notes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView userpic;
    private TextView userEmail, userName;
    Bundle bundle = new Bundle();
    android.support.v4.app.FragmentManager manager = getSupportFragmentManager();    //Initializing Fragment Manager.
    AddNoteFragment Fragment = new AddNoteFragment();
    android.support.v4.app.FragmentTransaction transaction;
    public static RecyclerView recyclerView,tagRecyclerView;
    public static NoteAdapter adapter;
    public static TagAdapter tagadapter;
    public static int i = 0;
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static ArrayList<DataModel> arraydata=new ArrayList<>();
    public static ArrayList<TagModel> tagData=new ArrayList<>();
    public static DatabaseReference rootreference,titlerefrence,noterefrence;
    public static String userId;
    public static ArrayList<String> keyStore=new ArrayList<>();
    public static FloatingActionButton fab;

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
        userEmail.setText(LoginActivity.email);
        Glide.with(getApplicationContext()).load(LoginActivity.pic)
                .thumbnail(0.5f)
                .crossFade()
                .transform(new CircularTranformation(Notes.this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userpic);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //tagRecyclerView.setHasFixedSize(true);
        //tagRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        tagadapter = new TagAdapter(Notes.this,AddNoteFragment.taglist);
        if(AddNoteFragment.taglist.size()==0)
        {
        }
         else
           {
            tagRecyclerView.setAdapter(tagadapter);
           }
        rootreference = database.getInstance().getReference("Your Note");
        // Creating new user node, which returns the unique key value
        userId = rootreference.push().getKey();
        keyStore.add(i,userId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Initialize a new instance of RecyclerView Adapter instance.
        rootreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraydata.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    arraydata.add(dataSnapshot1.getValue(DataModel.class));
                    adapter=new NoteAdapter(Notes.this,arraydata);
                }

                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Notes.this,"Something went wrong on firebase",Toast.LENGTH_SHORT).show();
            }
        });

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
    public void showlayoutforaddingnote()
    {
        //fab.setVisibility(View.INVISIBLE);
        getSupportActionBar().setTitle("Add Note");
        transaction = manager.beginTransaction();
        transaction.replace(R.id.recyclerViewXML, Fragment).commit();
        transaction.show(Fragment);
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
