package com.example.imagepro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Profile extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 2;
    ImageView profileImageView;
    FloatingActionButton floatingButton;
    Toolbar toolbar;
    TextInputEditText editUserName, editEmail, editPassword, ConfirmPassword;
    Button saveButton;

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
    }
   /* public String getLoggedInEmail() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("session", Context.MODE_PRIVATE);
        return sharedPreferences.getString( "email", "");
    }

    private void showLoggedInEmail() {
        String email = getLoggedInEmail();
        if(email.isEmpty()) {
            Toast.makeText(this, "Logged-in Email: " + email, Toast.LENGTH_SHORT).show();
        }    }*/
    private void onEdit() {
        SessionManager sm=new SessionManager(this);
        String email = sm.getLoggedInEmail();
        String userName = editUserName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String Confirm = ConfirmPassword.getText().toString();


        String emailPattern = "^\\S+@\\S+\\.\\S+$";
        if (!newEmail.matches(emailPattern) && newEmail.length()>0) {
            editEmail.setError("Invalid email address");
            return;
        }
        // Password validation
        if (password.length() < 8 && password.length()>0 ) {
            editPassword.setError("Password must be at least 8 characters long");
            return;
        }
        if (!password.equals(Confirm)) {
            ConfirmPassword.setError("Passwords do not match");
            return;
        }
        if(password.length()!=0 && Confirm.isEmpty()){
            ConfirmPassword.setError("This field is required");
        }



        // Call the background task to update user data
        ProfileBackgroundTask backgroundTask = new ProfileBackgroundTask(this);
        backgroundTask.execute("Profile", email, userName, newEmail, password);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setLoggedInEmail(newEmail);
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
