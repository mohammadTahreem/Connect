package com.devtahreem.testproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class UserAccount extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "UserAccount";

    private Button btnUpdateDetails, btnLogout;
    private FirebaseAuth mFirebaseAuth;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private TextView banner;
    private TextInputLayout name, age;
    private FirebaseDatabase mFirebase;
    private EditText name1, ageEditText;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private ImageView imageView, navImage;
    private Uri imageUri;
    private StorageReference storageReference;
    private EditText addressEditText, occupationEditText, aboutEditText;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Bitmap bitmap;
    private TextView navUsername;
    private String nameTextV;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        btnLogout = (Button) findViewById(R.id.logoutMenu);
        switch (item.getItemId()) {
            case R.id.logoutMenu: {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                Toast.makeText(UserAccount.this, "User Logged Out", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        banner = findViewById(R.id.banner);
        btnUpdateDetails = findViewById(R.id.btnUpdate);
        age = findViewById(R.id.age);
        name = findViewById(R.id.name);
        name1 = findViewById(R.id.name1);
        ageEditText = findViewById(R.id.ageEditText);
        imageView = findViewById(R.id.imageView);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        addressEditText = findViewById(R.id.editAddress);
        occupationEditText = findViewById(R.id.editOccupation);
        aboutEditText = findViewById(R.id.editAbout);

        View headerView = navigationView.getHeaderView(0);
        navImage = headerView.findViewById(R.id.UserImage);
        navUsername = headerView.findViewById(R.id.usernameNavigationView);

        databaseReference = FirebaseDatabase.getInstance().getReference(userId);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.nav_open,
                R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        navigationView.setCheckedItem(R.id.nav_account);
        navigationView.setNavigationItemSelectedListener(this);

        btnUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Write data to the database
                String nameEdit = name1.getText().toString().trim();
                String age1 = ageEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String occupation = occupationEditText.getText().toString().trim();
                String about = aboutEditText.getText().toString().trim();

                if (nameEdit != null && !nameEdit.isEmpty()) {
                    databaseReference.child("Name").setValue(nameEdit);
                }
                if (age1 != null && !age1.isEmpty()) {
                    databaseReference.child("Age").setValue(age1);
                }
                if (address != null && !address.isEmpty()) {
                    databaseReference.child("Address").setValue(address);
                }
                if (occupation != null && !occupation.isEmpty()) {
                    databaseReference.child("Occupation").setValue(occupation);
                }
                if (about != null && !about.isEmpty()) {
                    databaseReference.child("About").setValue(about);
                }

                Toast.makeText(UserAccount.this, "The details have been successfully updated", Toast.LENGTH_SHORT).show();

            }
        });


        //Gets the value from data base and adds it to the textViews
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TAG", "onDataChange: " + snapshot);

                if (snapshot.hasChild("Age")) {
                    String age = snapshot.child("Age").getValue().toString().trim();
                    ageEditText.setText(age.trim());
                }
                if (snapshot.hasChild("Name")) {
                    nameTextV = snapshot.child("Name").getValue().toString().trim();
                    name1.setText(nameTextV.trim());
                    banner.setText("Welcome! " + nameTextV.trim());
                    navUsername.setText(nameTextV.trim());
                }
                if (snapshot.hasChild("Address")) {
                    String address = snapshot.child("Address").getValue().toString().trim();
                    addressEditText.setText(address.trim());
                }
                if (snapshot.hasChild("Occupation")) {
                    String occupation = snapshot.child("Occupation").getValue().toString().trim();
                    occupationEditText.setText(occupation.trim());
                }
                if (snapshot.hasChild("About")) {
                    String about = snapshot.child("About").getValue().toString().trim();
                    aboutEditText.setText(about.trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserAccount.this, "Check your connection", Toast.LENGTH_SHORT).show();
            }
        });


        //Get the image from storage and display in imageView
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageReference = mStorageRef.child(userId).child("displayPicture_200x200.jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...
                            Log.d("TAG", "onSuccess: " + localFile + "  :This is the tasksnap: " + taskSnapshot);
                            bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            imageView.setImageBitmap(bitmap);
                            navImage.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        storageReference = mStorageRef.child(userId).child("displayPicture.jpg");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading file....");
        progressDialog.show();
        progressDialog.setCancelable(false);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(UserAccount.this, "Faied to upload", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progressPercentage = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Percentage: " + (int) progressPercentage + "%");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_account: {
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.nav_home: {
                Intent intent = new Intent(UserAccount.this, HomePage.class);
                startActivity(intent);
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}