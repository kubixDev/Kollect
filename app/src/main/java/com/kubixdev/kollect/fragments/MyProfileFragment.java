package com.kubixdev.kollect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.activities.SettingsActivity;
import com.kubixdev.kollect.model.User;
import com.kubixdev.kollect.utils.BlurUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MyProfileFragment extends Fragment {
    AppCompatButton settingsButton;
    TextView userNameLabel;
    ImageView backgroundPictureView;
    ImageView profilePictureView;

    // stores current user data taken from main activity (to avoid unnecessary database reads)
    private User currentUserData;

    public MyProfileFragment(){}

    public void passUserData(User user) {
        this.currentUserData = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        userNameLabel = view.findViewById(R.id.userNameLabel);
        settingsButton = view.findViewById(R.id.settingsButton);

        userNameLabel.setText(currentUserData.getUsername());

        // loads and displays profile image
        profilePictureView = view.findViewById(R.id.profilePictureView);
        loadProfileImage(currentUserData.getProfileImage());

        // blurs background image
        backgroundPictureView = view.findViewById(R.id.backgroundPictureView);
        BlurUtils.applyBlur(requireContext(), backgroundPictureView);

        // handle settings activity button
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        });

        return view;
    }

    private void loadProfileImage(String fileName) {
        if (fileName != null) {
            StorageReference gsReference = FirebaseStorage.getInstance().getReference().child("Users/" + fileName);

            try {
                // creates a temporary file to store the downloaded image
                File localFile = File.createTempFile("images", "jpg");

                // downloads the image
                gsReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    // loads the downloaded image using Glide
                    Glide.with(requireContext())
                            .load(localFile)
                            .placeholder(R.drawable.placeholderimage)
                            .into(profilePictureView);

                }).addOnFailureListener(exception -> {
                    Toast.makeText(requireContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}