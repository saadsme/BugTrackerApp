package com.example.bugtracker;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    EditText et_username, et_password;
    TextView tv_resetPassword;
    Button login, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FireStoreServices.initFireStore();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_username = findViewById(R.id.ET_user);
        et_password = findViewById(R.id.ET_pass);
        tv_resetPassword = findViewById(R.id.tv_ForgetPassword);
        login = findViewById(R.id.Login);
        signup = findViewById(R.id.signup);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        tv_resetPassword.setOnClickListener(this);
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn() {
        Toast.makeText(getApplicationContext(), "Trying to log in",
                Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(et_username.getText().toString(),
                        et_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent i = new Intent(getApplicationContext(), BugListActive.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("username", user.getEmail());
                            startActivity(i);
                        } else {
// If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
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
    public void onClick(View v) {
        if (v.getId() == R.id.Login) {
           try
           {
               signIn();
           }
           catch(Exception e)
           {
               Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT);
           }

        }
        if (v.getId() == R.id.signup)
        {
            Intent signUp = new Intent(this, SignUpActivity.class);
            startActivity(signUp);
        }
        if(v.getId() == R.id.tv_ForgetPassword)
        {
            try {
                Toast.makeText(LoginActivity.this,"Sending Password Reset Email",Toast.LENGTH_LONG).show();
                String email = et_username.getText().toString();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            // if isSuccessful then done message will be shown
                            // and you can change the password
                            Toast.makeText(LoginActivity.this,"Done sent",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"Error Occurred",Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,"Error Failed",Toast.LENGTH_LONG).show();
                    }
                });

            }
            catch(Exception e)
            {
                Toast.makeText(LoginActivity.this,"Please enter email!",Toast.LENGTH_LONG).show();
            }

        }
    }
}