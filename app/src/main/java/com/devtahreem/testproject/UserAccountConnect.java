package com.devtahreem.testproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testproject.R;
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

public class UserAccountConnect extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private ImageView imageViewConnect;
    private TextView nameTextView, ageTexTView, occupationTextView, addressTextView, aboutTextView, toolbarTextViewConnect;
    private static final String TAG = "UserAccountConnect";

    private DatabaseReference mdatabaseReferenceConnect;

    private Button connectButton;
    private NavigationView mNavigation;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Bitmap bitmap2;

    private ImageView navImageHome;
    private TextView navUserName;
    private String connectName;

    private String userIdCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_connect);

        String currentUserId = FirebaseAuth.getInstance().getUid();
        nameTextView = findViewById(R.id.nameConnect);
        ageTexTView = findViewById(R.id.ageConnect);
        addressTextView = findViewById(R.id.addressConnect);
        occupationTextView = findViewById(R.id.occupationConnect);
        aboutTextView = findViewById(R.id.aboutConnect);
        connectButton = findViewById(R.id.buttonConnect);
        imageViewConnect = findViewById(R.id.imageViewConnect);

        mNavigation = findViewById(R.id.nav_view);

        View viewHeader = mNavigation.getHeaderView(0);
        navUserName = viewHeader.findViewById(R.id.usernameNavigationView);
        navImageHome = viewHeader.findViewById(R.id.UserImage);

        toolbar = findViewById(R.id.toolbarConnect);
        toolbarTextViewConnect = findViewById(R.id.toolbarTextViewConnect);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Get the ID of the user by clicking the card on recyclerView
        Intent bit = getIntent();
        Bundle bw = bit.getExtras();

        if (bw != null) {
            userIdCard = (String) bw.get("User id");
            Toast.makeText(this, userIdCard, Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "The id of the current user is ************************  " + userIdCard);

        //TO get the image of the selected user from card-------------------------------
        StorageReference cardUserImageStorage = FirebaseStorage.getInstance().getReference();
        StorageReference mgStorageCardUser = FirebaseStorage.getInstance().getReference();

        final File localFile4;
        try {
            localFile4 = File.createTempFile("images", "jpg");
            cardUserImageStorage = mgStorageCardUser.child(userIdCard).child("displayPicture_200x200.jpg");
            cardUserImageStorage.getFile(localFile4)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap1 = BitmapFactory.decodeFile(localFile4.getAbsolutePath());
                            imageViewConnect.setImageBitmap(bitmap1);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //------------------------------------------------


        //used to get the details of the current user
        mdatabaseReferenceConnect = FirebaseDatabase.getInstance().getReference(userIdCard);
        mdatabaseReferenceConnect.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                connectName = snapshot.child("Name").getValue().toString().trim();
                final String connectAge = snapshot.child("Age").getValue().toString().trim();
                final String connectAddress = snapshot.child("Address").getValue().toString().trim();
                final String connectOccupation = snapshot.child("Occupation").getValue().toString().trim();
                final String connectAbout = snapshot.child("About").getValue().toString().trim();

                nameTextView.setText(connectName);
                toolbarTextViewConnect.setText(connectName.trim() + "'s Profile");
                aboutTextView.setText(connectAbout);
                occupationTextView.setText(connectOccupation);
                addressTextView.setText(connectAddress);
                ageTexTView.setText(connectAge);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //-----------------------------------------------
        //for importing the image of the logged in user
        StorageReference storageReferenceCurrentUser = FirebaseStorage.getInstance().getReference();
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        final File localFile3;
        try {
            localFile3 = File.createTempFile("images", "jpg");
            storageReferenceCurrentUser = mStorage.child(currentUserId).child("displayPicture_200x200.jpg");
            storageReferenceCurrentUser.getFile(localFile3)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile3.getAbsolutePath());
                            Log.d(TAG, "onSuccess: Bitmap is" + bitmap);
                            navImageHome.setImageBitmap(bitmap);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        //-----------------------------------------------


        //Action bar-----------------------------------------
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
        mNavigation.setNavigationItemSelectedListener(this);
        //----------------------------------------------------


        //To import the name of the current logged in user
        DatabaseReference mfirebaseName = FirebaseDatabase.getInstance().getReference(currentUserId);
        mfirebaseName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String navNameTextView = snapshot.child("Name").getValue().toString();
                navUserName.setText(navNameTextView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Connect button functionality----------------------


        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Build a notification-------------------------------------------------------------
                final String message = "This is just a test notification";
                final NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        UserAccountConnect.this)
                        .setSmallIcon(R.drawable.ic_home_icon)
                        .setContentText(message)
                        .setContentTitle("Test")
                        .setAutoCancel(true);

                Intent intent = new Intent(getApplicationContext(), getClass());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("message", message);

                PendingIntent pendingIntent = PendingIntent.getActivity(UserAccountConnect.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
                //---------------------------------------------------------------------------------
                Toast.makeText(UserAccountConnect.this, "The request was sent to " + connectName, Toast.LENGTH_SHORT).show();
                connectButton.setText("Requested");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Button logout = findViewById(R.id.logoutMenu);
        if (item.getItemId() == R.id.logoutMenu) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
            Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_account:
                Intent intent2 = new Intent(this, UserAccount.class);
                startActivity(intent2);
                break;

            case R.id.nav_home:
                Intent intent1 = new Intent(this, HomePage.class);
                startActivity(intent1);
                break;
        }
        return false;
    }
}