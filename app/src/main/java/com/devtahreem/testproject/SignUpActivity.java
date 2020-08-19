package com.devtahreem.testproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.testproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = null;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    Button btnSignUp, btnSignIn;
    String userEmail, userID;
    private EditText txtPassword, txtConfPass;
    private EditText phoneNumber, txtEmail;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private long mBackPressed;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(this, com.devtahreem.testproject.UserAccount.class));
            userEmail = firebaseUser.getEmail();
        }
    }

    public String getUserEmail() {
        return userEmail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPassword = findViewById(R.id.passwordEditText);
        btnSignUp = findViewById(R.id.signUpButton);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtConfPass = findViewById(R.id.confPasswordEditText);
        txtEmail = findViewById(R.id.emailEditText);
        firebaseAuth = FirebaseAuth.getInstance();


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = txtPassword.getText().toString().trim();
                final String email = txtEmail.getText().toString().trim();
                final String confirmPassword = txtConfPass.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.equals(confirmPassword)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getApplicationContext(), UserAccount.class));
                                        Toast.makeText(SignUpActivity.this, "Registration done", Toast.LENGTH_SHORT).show();
                                        userID = firebaseAuth.getUid();
                                        Log.d(TAG, "getUserID: " + userID);

                                        databaseReference = FirebaseDatabase.getInstance().getReference(userID);
                                        databaseReference.child("Name").setValue("");
                                        databaseReference.child("Address").setValue("");
                                        databaseReference.child("Age").setValue("");
                                        databaseReference.child("Occupation").setValue("");
                                        databaseReference.child("About").setValue("");


                                    } else {
                                        Toast.makeText(SignUpActivity.this, "There was an error retrieving the responses.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
            }
        });
    }
}