package com.devtahreem.testproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "";
    private DatabaseReference databaseReference, databaseReferenceAllUsers;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private Button logout;

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ArrayList<UserCardDetails> userCardDetailsArrayList = new ArrayList<>();
    private StorageReference mStorage, storageReference;
    private Bitmap bitmap;
    private ImageView navImageHome;
    private TextView navTextViewHome;
    private EditText searchUser;
    private Button bottomHome, bottomChat, bottomNotification;

    @Override
    public void onBackPressed() {

        searchUser = findViewById(R.id.searchHomeFragment);
        String searchedUser = searchUser.getText().toString();

        if (!searchedUser.isEmpty()) {
            searchUser.setText("");
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                moveTaskToBack(true);
                return;
            } else {
                Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Button search = findViewById(R.id.search);
        logout = (Button) findViewById(R.id.logoutMenu);
        switch (item.getItemId()) {
            case R.id.logoutMenu: {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                Toast.makeText(HomePage.this, "User Logged Out", Toast.LENGTH_SHORT).show();
                finish();
            }
            case R.id.nav_home: {
                break;
            }
            case R.id.nav_account: {
                Intent intent = new Intent(HomePage.this, UserAccount.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        firebaseAuth = FirebaseAuth.getInstance();
        final String userId = firebaseAuth.getUid().toString();
        Log.d(TAG, "The current User ID is " + userId);
        databaseReference = FirebaseDatabase.getInstance().getReference(userId);

        storageReference = FirebaseStorage.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar1);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        searchUser = findViewById(R.id.searchHomeFragment);

        //bottomChat = findViewById(R.id.bottom_navigation_chat);
        //bottomHome = findViewById(R.id.bottom_navigation_home);
        //bottomNotification = findViewById(R.id.bottom_navigation_notifications);



        View viewHomeHeader = navigationView.getHeaderView(0);
        navImageHome = viewHomeHeader.findViewById(R.id.UserImage);
        navTextViewHome = viewHomeHeader.findViewById(R.id.usernameNavigationView);


        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });


        final File localFile2;
        try {
            localFile2 = File.createTempFile("images", "jpg");
            storageReference = mStorage.child(userId).child("displayPicture_200x200.jpg");
            storageReference.getFile(localFile2)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            bitmap = BitmapFactory.decodeFile(localFile2.getAbsolutePath());
                            Log.d(TAG, "onSuccess: Bitmap is" + bitmap);
                            navImageHome.setImageBitmap(bitmap);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String usernameNavigation = snapshot.child("Name").getValue().toString().trim();
                navTextViewHome.setText(usernameNavigation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePage.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });

        //Toolbar customization----------
        TextView toolbarTextView = findViewById(R.id.toolbarTextView);
        toolbarTextView.setText("Home");


        //Progress bar on the start of page---------
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Retrieving users you can connect with.");
        progressDialog.show();
        setSupportActionBar(toolbar);
        //--------------

        //NavigationMenu Starter-------------
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.nav_open,
                R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);

        //when the user doesn't have any display picture uploaded
        final Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.dp1);

        databaseReferenceAllUsers = FirebaseDatabase.getInstance().getReference();

        // Read from the database and display in the recyclerView
        databaseReferenceAllUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    progressDialog.dismiss();
                    final String allUserId = ds.getKey();
                    final String name = ds.child("Name").getValue().toString();
                    final String address = ds.child("Address").getValue().toString();
                    final String age = ds.child("Age").getValue().toString();
                    final String occupation = ds.child("Occupation").getValue().toString();
                    Log.d(TAG, "The name of user is " + name
                            + " and the age is " + age
                            + ", the occupation is " + occupation
                            + " and the address is " + address
                            + "***** the user id is " + allUserId);


                    if (userId.equals(allUserId)) {
                        Log.d(TAG, "onDataChange: The user cannot be printed");
                    } else {

                        storageReference = FirebaseStorage.getInstance().getReference();
                        mStorage = FirebaseStorage.getInstance().getReference();

                        try {
                            final File localFile = File.createTempFile("images", "jpg");

                            assert allUserId != null;
                            storageReference = mStorage.child(allUserId).child("displayPicture_200x200.jpg");
                            mRecyclerView = findViewById(R.id.recyclerView);
                            mLayoutManager = new LinearLayoutManager(HomePage.this);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setHasFixedSize(true);

                            storageReference.getFile(localFile)
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                            bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                            Log.d(TAG, "onSuccess: Bitmap is" + bitmap);

                                            userCardDetailsArrayList.add(new UserCardDetails(bitmap, name, age, address, occupation, allUserId));
                                            mAdapter = new UserCardAdapter(userCardDetailsArrayList);
                                            mRecyclerView.setAdapter(mAdapter);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    userCardDetailsArrayList.add(new UserCardDetails(bitmap1, name, age, address, occupation, allUserId));
                                    mAdapter = new UserCardAdapter(userCardDetailsArrayList);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
                Log.w(TAG, "Failed to read value. Check your internet connection", error.toException());
            }
        });

    }

    private void filter(String text) {
        ArrayList<UserCardDetails> filterList = new ArrayList<>();

        for (UserCardDetails item : userCardDetailsArrayList) {
            if (text.toLowerCase().trim().isEmpty()) {

            }
            if (item.getName().toLowerCase().trim().contains(text.toLowerCase())) {
                filterList.add(item);
            }
        }
        mAdapter = new UserCardAdapter(filterList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home: {
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.nav_account: {
                Intent intent = new Intent(HomePage.this, UserAccount.class);
                startActivity(intent);
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}