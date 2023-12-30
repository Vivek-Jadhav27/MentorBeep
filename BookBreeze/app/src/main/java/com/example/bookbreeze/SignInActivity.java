package com.example.bookbreeze;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    EditText  signinemail , signinpassword, signinconpassword , signinusername;
    Button signinbtn;
    TextView logintext;
    private FirebaseAuth mAuth;

    DatabaseReference database;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser= mAuth.getCurrentUser();
        if(currentuser!= null){
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signinemail = findViewById(R.id.signinemail);
        signinpassword = findViewById(R.id.signinpassword);
        signinconpassword = findViewById(R.id.signinconfirmpassword);
        signinusername = findViewById(R.id.signinusername);
        signinbtn = findViewById(R.id.signinbutton);
        logintext = findViewById(R.id.logintext);

        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,LoginActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signinemail.getText().toString();
                String password = signinpassword.getText().toString();
                String username = signinusername.getText().toString();
                if (validateEmail(email) & validatePassword(password) & validateUsername(username) & validateConfirmPassword()) {

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser currentuser= mAuth.getCurrentUser();
                                User user = new User();
                                if (currentuser != null) {
                                    user.setUserId(currentuser.getUid());
                                }
                                user.setUserEmail(email);
                                user.setUserPassword(password);
                                user.setUserName(username);

                                if (currentuser != null) {
                                    database.child("User").child(currentuser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> databaseTask) {
                                            if (databaseTask.isSuccessful()) {
                                                Toast.makeText(SignInActivity.this, "Database complete", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Database operation failed
                                                Exception databaseException = databaseTask.getException();
                                                Log.e(TAG, "onComplete: Database operation failed", databaseException);
                                                Toast.makeText(SignInActivity.this, "Database operation failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                Log.d(TAG, "onCreate: SignInActivity created");
                                        startActivity(new Intent(SignInActivity.this , MainActivity.class));
                                        Toast.makeText(SignInActivity.this, "Authentication success", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Exception exception = task.getException();
                                        Log.e(TAG, "onComplete: ", exception);
                                        Toast.makeText(SignInActivity.this, "Authentication failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }}
                    });
                }
            }
        });


    }

    private boolean validateEmail(String email){
        if (signinemail.getText().toString().isEmpty()){
            signinemail.setError("Empty Email");
            return false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signinemail.setError("Invalid Email");
            return false;
        }
        return true;
    }
    private boolean validatePassword(String password){
        if(signinpassword.getText().toString().isEmpty()){
            signinpassword.setError("Empty Password");
            return false;
        }
        else {
            return password.length() >= 8;
        }
    }
    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            signinusername.setError("Empty Username");
            return false;
        }
        // Add any additional username validation logic here if needed
        return true;
    }

    private boolean validateConfirmPassword() {
        String password = signinpassword.getText().toString();
        String confirmPassword = signinconpassword.getText().toString();

        if (!validatePassword(password)) {
            if (confirmPassword.isEmpty()){
                signinconpassword.setError("Empty");
                return false;
            }
            return false;
        }

        if (!confirmPassword.equals(password)) {
            signinconpassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

}