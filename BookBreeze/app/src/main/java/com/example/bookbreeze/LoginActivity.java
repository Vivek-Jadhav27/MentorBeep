package com.example.bookbreeze;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText  loginemail , loginpassword;
    Button loginButton;
    TextView signinText;
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser!= null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginemail = findViewById(R.id.loginemail);
        loginpassword = findViewById(R.id.loginpassword);
        loginButton = findViewById(R.id.loginbutton);
        signinText = findViewById(R.id.signintext);

        signinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this , SignInActivity.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatePassword(loginpassword.getText().toString()) & validateEmail(loginemail.getText().toString())){
                    String email = loginemail.getText().toString();
                    String password = loginpassword.getText().toString();
                    mAuth.signInWithEmailAndPassword(email ,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this , MainActivity.class));
                            }
                            else {
                                Exception exception = task.getException();
                                Log.e(TAG, "onComplete: ", exception);
                                Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    closeKeyboardIfOutsideEditText(loginemail , event);
                    closeKeyboardIfOutsideEditText(loginpassword, event);
                }
                view.performClick(); // Call performClick for the touch event
                return false; // Allow the event to continue propagating
            }
        });
    }

    private void closeKeyboardIfOutsideEditText(EditText editText, MotionEvent event) {
        if (editText.isFocused()) {
            Rect outRect = new Rect();
            editText.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                // The touch was outside the EditText; close the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }
    private  boolean validateEmail(String email){
        if (loginemail.getText().toString().isEmpty()){
            loginemail.setError("Empty Field");
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password){
        if (loginpassword.getText().toString().isEmpty()) {
            loginpassword.setError("Empty Field");
            return false;
        }
        return  true;
    }
}