package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.imagepro.BackGroundWork.ProfileBackgroundTask;
import com.example.imagepro.BackGroundWork.SessionBackgroundTask;
import com.example.imagepro.BackGroundWork.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Profile extends AppCompatActivity implements SessionBackgroundTask.OnTaskCompleted {
    private static final int REQUEST_GALLERY = 2;
    ImageView profileImageView;
    FloatingActionButton floatingButton;
    Toolbar toolbar;
    TextInputEditText editUserName, editEmail, editPassword, ConfirmPassword;
    Button saveButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        floatingButton = findViewById(R.id.change_photo_floating_button);
        profileImageView = findViewById(R.id.profile_photo_image_view);
        toolbar = findViewById(R.id.profile_tool_bar);
        editUserName = findViewById(R.id.edit_user_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        ConfirmPassword = findViewById(R.id.edit_confirm_password);
        saveButton = findViewById(R.id.save_edit_button);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Profile");
        floatingButton.setOnClickListener(v -> showImageSelectionDialog());
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEdit();
            }
        });
        cancelButton = findViewById(R.id.cancel_edit_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentClass.goToActivity(Profile.this,HomeActivity.class);
            }
        });

        // Call the method to retrieve logged-in data
        retrieveLoggedInData();
        SessionManager sm=new SessionManager(this);
        editEmail.setText(sm.getLoggedInEmail());
    }

    public void retrieveLoggedInData() {
        String email = getLoggedInEmail();

        // Create an instance of SessionManager
        SessionManager sessionManager = new SessionManager(this);

        // Create an instance of SessionBackgroundTask
        SessionBackgroundTask backgroundTask = new SessionBackgroundTask(email, sessionManager,this);

        // Execute the background task
        backgroundTask.execute(email);
    }

    // Implement the methods of OnTaskCompleted interface
    @Override
    public void onTaskCompleted(JSONObject jsonObject) {
        // This method is called when the background task is completed
        // Retrieve the username and password from SessionManager and update the UI fields
        SessionManager sm=new SessionManager(this);
        String username =sm.getLoggedInUsername();
        String password = sm.getLoggedInPassword();

        // Update the UI fields with the retrieved data
        editEmail.setText(sm.getLoggedInEmail());
        editUserName.setText(username);
    }

    @Override
    public void onError(String errorMessage) {
        // This method is called when an error occurs during the background task
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
    public String getLoggedInEmail() {
        SessionManager sm = new SessionManager(this);
        return sm.getLoggedInEmail();
    }

    private void onEdit() {
        String email = getLoggedInEmail();
        String userName = editUserName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String Confirm = ConfirmPassword.getText().toString();

        String emailPattern = "^\\S+@\\S+\\.\\S+$";

        if (!newEmail.matches(emailPattern) && newEmail.length() > 0) {
            editEmail.setError("Invalid email address");
            return;
        }

        // Password validation
        if (password.length() < 8 && password.length() > 0) {
            editPassword.setError("Password must be at least 8 characters long");
            return;
        }

        if (!password.equals(Confirm)) {
            ConfirmPassword.setError("Passwords do not match");
            return;
        }

        if (password.length() != 0 && Confirm.isEmpty()) {
            ConfirmPassword.setError("This field is required");
        }

        // Call the background task to update user data
        ProfileBackgroundTask backgroundTask = new ProfileBackgroundTask(this);
        backgroundTask.execute("Profile", email, userName, newEmail, password);

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        if (!newEmail.isEmpty()) {
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            sessionManager.setLoggedInEmail(newEmail);
        }
    }

    private void showImageSelectionDialog() {
        List<ImageSelectionOption> options = new ArrayList<>();
        options.add(new ImageSelectionOption(R.drawable.baseline_photo_library_24, "Gallery"));
        ImageSelectionDialogFragment dialogFragment = ImageSelectionDialogFragment.newInstance(options);
        dialogFragment.setOnOptionClickListener(this::handleImageSelectionOption);
        dialogFragment.show(getSupportFragmentManager(), "image_selection_dialog");
    }

    private void handleImageSelectionOption(ImageSelectionOption option) {
        if (option.getOptionText().equals("Gallery")) {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                Uri imageUri = data.getData();
                profileImageView.setImageURI(imageUri);
            }
        }
    }
    public static class ImageSelectionDialogFragment extends BottomSheetDialogFragment {

        private List<ImageSelectionOption> options;
        private OnOptionClickListener onOptionClickListener;

        public static ImageSelectionDialogFragment newInstance(List<ImageSelectionOption> options) {
            ImageSelectionDialogFragment fragment = new ImageSelectionDialogFragment();
            fragment.options = options;
            return fragment;
        }

        public void setOnOptionClickListener(OnOptionClickListener listener) {
            onOptionClickListener = listener;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_image_selection, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            RecyclerView optionsRecyclerView = view.findViewById(R.id.optionsRecyclerView);
            optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ImageSelectionAdapter adapter = new ImageSelectionAdapter(options, option -> {
                if (onOptionClickListener != null) {
                    onOptionClickListener.onOptionClick(option);
                }
                dismiss();
            });
            optionsRecyclerView.setAdapter(adapter);
        }

        public interface OnOptionClickListener {
            void onOptionClick(ImageSelectionOption option);
        }
    }



}
