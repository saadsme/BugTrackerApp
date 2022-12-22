package com.example.bugtracker;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
private FirebaseAuth mAuth;
        EditText et_username, et_password, et_nickname;
        Button  signup;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        et_username = findViewById(R.id.ET_user);
        et_password = findViewById(R.id.ET_pass);
        et_nickname = findViewById(R.id.ET_nickname);
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(this);
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {

                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.d("FIREBASE", "Fetching FCM registration token failed");
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    Log.d("FIREBASE", token);
                   // Toast.makeText(getApplicationContext(), "Reg token" + token, Toast.LENGTH_SHORT).show();
                }
            });

    FirebaseMessaging.getInstance().subscribeToTopic("application")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    String msg = "Subscribed";
                    if (!task.isSuccessful()) {
                        msg = "Subscribe failed";
                    }
                    Log.d("Firebase", msg);
                   // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }



public void getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
// Name, email address, and profile photo Url
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();
        }
        }


    @Override
    public void onClick(View view)
        {
            mAuth.createUserWithEmailAndPassword(et_username.getText().toString(),
                            et_password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
// Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getApplicationContext(), "Create User With Email :success\nPlease login", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(et_nickname.getText().toString())
                                        .build();
                                user.updateProfile(profileUpdates);
                                finish();
                            }
                            else {
// If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

}