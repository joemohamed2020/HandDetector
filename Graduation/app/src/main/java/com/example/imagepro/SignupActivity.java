package com.example.imagepro;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView textView ;
    TextInputEditText email,password,name,confirmPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        toolbar = findViewById(R.id.signup_tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Signlator");
        name=findViewById(R.id.editTextTextPersonName);
        email=findViewById(R.id.editTextTextPersonName2);
        password=findViewById(R.id.editTextTextPersonName3);
        confirmPass=findViewById(R.id.editTextTextPersonName4);
        textView = findViewById(R.id.haveAccount);
        String text = "Already have an Account?";
        SpannableString ss =  new SpannableString(text);
        ClickableSpan clickableSpan =  new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                IntentClass.goToActivity(SignupActivity.this,SigninActivity.class);
            }
        };
        ss.setSpan(clickableSpan,16,24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> OnSignup(view));
    }

    public void OnSignup(View view) {
        String Name = name.getText().toString();
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        String Confirm = confirmPass.getText().toString();
        String type = "Signup";

        // Perform input validations
        if (Name.isEmpty()) {
            name.setError("Name is required");
            return;
        }
        if (Email.isEmpty()) {
            email.setError("Email is required");
            return;
        }
        // Email validation
        String emailPattern = "^\\S+@\\S+\\.\\S+$";
        if (!Email.matches(emailPattern)) {
            email.setError("Invalid email address");
            return;
        }
        if (Password.isEmpty()) {
            password.setError("Password is required");
            return;
        }
        // Password validation
        if (Password.length() < 8) {
            password.setError("Password must be at least 8 characters long");
            return;
        }
        if (!Password.equals(Confirm)) {
            confirmPass.setError("Passwords do not match");
            return;
        }


        // Input is valid, proceed with signup
        SignUpBackgroundTask checkEmailTask = new SignUpBackgroundTask(
                this,
                () -> {
                    // Handle email does not exist

                    email.setError("Email Already Exists");
                },

                () -> {

                    Intent intent = new Intent(this, SigninActivity.class);
                    startActivity(intent);
                    finish(); // Optional: Finish the current activity
                },
                error -> {
                    // Handle task failed
                    Toast.makeText(this,  error, Toast.LENGTH_SHORT).show();
                }
        );
        checkEmailTask.execute(type, Name, Email , Password, Confirm);
    }


}


