package com.example.imagepro;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class SigninActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextInputEditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        toolbar = findViewById(R.id.signin_tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Signlator");

        TextView textView = findViewById(R.id.textView);
        String text = "Are you new user? Register";
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
        button.setOnClickListener(this::OnLogin);

        //goToHome();

    }

    public void OnLogin(View view) {
        String Email = Objects.requireNonNull(email.getText()).toString();
        String Password = Objects.requireNonNull(password.getText()).toString();
        String type = "login";

        // Perform input validations
        if (Email.isEmpty()) {
            email.setError("Email is required");
            return;
        }

        if (Password.isEmpty()) {
            password.setError("Password is required");
            return;
        }

        // Check if email exists
        SignInBackgroundTask checkEmailTask = new SignInBackgroundTask(
                this,
                () -> {
                    // Handle email does not exist
                    email.setError("Email does not exist");
                    Log.d("SigninActivity","ana hna ya jhon");
                },
                () -> {
                    // Handle incorrect password
                    password.setError("Incorrect password");

                },
                () -> {
                    SessionManager sm = new SessionManager(SigninActivity.this);
                    sm.setLoggedInEmail(Email);
                    Intent intent = new Intent(SigninActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Optional: Finish the current activity
                },
                error -> {
                    // Handle task failed
                    Toast.makeText(this,  error, Toast.LENGTH_SHORT).show();
                }
        );
        checkEmailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, type, Email, Password);

    }

}