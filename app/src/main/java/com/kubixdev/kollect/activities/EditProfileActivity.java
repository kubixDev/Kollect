package com.kubixdev.kollect.activities;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.model.User;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {
    AppCompatButton pickProfilePictureButton, saveDetailsButton;
    EditText usernameInput;
    ImageView backButton;

    private User currentUserData;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        pickProfilePictureButton = findViewById(R.id.pickProfilePictureButton);
        saveDetailsButton = findViewById(R.id.saveDetailsButton);
        backButton = findViewById(R.id.backButton);
        usernameInput = findViewById(R.id.usernameInput);

        // initialize
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        loadUserData();

        // handle back button click
        backButton.setOnClickListener(v -> {
            finish();
        });

        // handle save details button click
        saveDetailsButton.setOnClickListener(v -> {
            if (currentUserData != null) {
                String newUsername = usernameInput.getText().toString().trim();
                currentUserData.setUsername(newUsername);
                Toast.makeText(EditProfileActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // handle pick pfp button click
        pickProfilePictureButton.setOnClickListener(v -> {
            openImagePicker();
        });
    }

    // load user data
    private void loadUserData() {
        if (currentUser != null) {
            User.loadUserData(currentUser.getUid(), new User.UserDataLoadListener() {
                @Override
                public void onUserDataLoaded(User user) {
                    currentUserData = user;

                    // update UI with user data
                    if (currentUserData != null) {
                        usernameInput.setText(currentUserData.getUsername());
                    }
                }

                @Override
                public void onUserDataLoadFailed(String error) {
                    Toast.makeText(EditProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        if (currentUserData != null) {
            String currentImageName = currentUserData.getProfileImage();

            if (currentImageName != null && !currentImageName.isEmpty() && !currentImageName.equals("default.jpg")) {
                StorageReference currentImageRef = storageReference.child("Users/" + currentImageName);

                currentImageRef.delete().addOnSuccessListener(aVoid -> {
                    uploadNewImage(imageUri);
                }).addOnFailureListener(exception -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to delete current profile picture", Toast.LENGTH_SHORT).show();
                    uploadNewImage(imageUri);
                });
            }
            else {
                uploadNewImage(imageUri);
            }
        }
    }

    private void uploadNewImage(Uri imageUri) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = currentUser.getUid() + "_" + timestamp + ".jpg";

        StorageReference imageRef = storageReference.child("Users/" + imageName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        currentUserData.setProfileImage(imageName);
                        Toast.makeText(EditProfileActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}