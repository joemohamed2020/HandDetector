package com.example.imagepro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.imagepro.BackGroundWork.SessionBackgroundTask;
import com.example.imagepro.BackGroundWork.SessionManager;
import com.example.imagepro.BackGroundWork.SignInBackgroundTask;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SigninActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextInputEditText email, password;
    SessionManager sessionManager;
    private boolean isBackPressedOnce =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        toolbar = findViewById(R.id.signin_tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Signlator");

        sessionManager = new SessionManager(this);

        TextView textView = findViewById(R.id.textView);
        String text = "Are you a new user? Register";
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                IntentClass.goToActivity(SigninActivity.this, SignupActivity.class);
            }
        };
        spannableString.setSpan(clickableSpan, 18, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        email = findViewById(R.id.editTextTextPersonName);
        password = findViewById(R.id.editTextTextPersonName2);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> onLogin(view));

        if (sessionManager.isLoggedIn()) {
            // User is already logged in, redirect to HomeActivity
            goToHome();
        }
    }

    public void onLogin(View view) {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String type = "login";

        // Perform input validations
        if (emailText.isEmpty()) {
            email.setError("Email is required");
            return;
        }

        if (passwordText.isEmpty()) {
            password.setError("Password is required");
            return;
        }

        // Check if email exists
        SignInBackgroundTask checkEmailTask = new SignInBackgroundTask(
                this,
                () -> {
                    // Handle email does not exist
                    email.setError("Email does not exist");
                },
                () -> {
                    // Handle incorrect password
                    password.setError("Incorrect password");
                },
                () -> {
                    // Handle successful login
                    sessionManager.setLoggedIn(true);
                    sessionManager.setLoggedInEmail(emailText);
                    goToHome();
                },
                error -> {
                    // Handle task failed
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
        );
        checkEmailTask.execute(type, emailText, passwordText);
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity
    }

    @Override
    public void onBackPressed() {
        if (isBackPressedOnce){
            this.finish();
        }
        else {
            Toast.makeText(this,"Back Again To Exit",Toast.LENGTH_SHORT).show();
            isBackPressedOnce = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isBackPressedOnce = false;
                }
            },2000);
        }
    }
}
