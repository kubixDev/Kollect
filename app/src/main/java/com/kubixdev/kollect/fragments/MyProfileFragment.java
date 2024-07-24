package com.kubixdev.kollect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.activities.SettingsActivity;
import com.kubixdev.kollect.model.Photocard;
import com.kubixdev.kollect.model.User;
import com.kubixdev.kollect.utils.BlurUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyProfileFragment extends Fragment {
    AppCompatButton settingsButton;
    TextView userNameLabel, ownedPhotocardsLabel, favoriteIdolLabel;
    ImageView backgroundPictureView;
    ImageView profilePictureView;

    // stores current user data taken from main activity (to avoid unnecessary database reads)
    private User currentUserData;
    private String favoriteIdol = "N/A";

    public MyProfileFragment(){}

    public void passUserData(User user) {
        this.currentUserData = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        userNameLabel = view.findViewById(R.id.userNameLabel);
        ownedPhotocardsLabel = view.findViewById(R.id.ownedPhotocardsLabel);
        favoriteIdolLabel = view.findViewById(R.id.favoriteIdolLabel);
        settingsButton = view.findViewById(R.id.settingsButton);

        if (currentUserData != null) {
            userNameLabel.setText(currentUserData.getUsername());

            // loads and displays profile image
            profilePictureView = view.findViewById(R.id.profilePictureView);
            loadProfileImage(currentUserData.getProfileImage());
        }

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

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();

        if (!favoriteIdol.equals("N/A")) {
            favoriteIdolLabel.setText(favoriteIdol);
        }
    }

    private void loadUserData() {
        if (currentUserData != null) {
            User.loadUserData(FirebaseAuth.getInstance().getCurrentUser().getUid(), new User.UserDataLoadListener() {
                @Override
                public void onUserDataLoaded(User user) {
                    currentUserData = user;

                    // update UI with user data
                    if (currentUserData != null) {
                        userNameLabel.setText(currentUserData.getUsername());
                        loadProfileImage(currentUserData.getProfileImage());

                        // count owned photocards and find most popular idol
                        int ownedPhotocardsCount = currentUserData.getOwnedPhotocardIds().size();
                        ownedPhotocardsLabel.setText(String.valueOf(ownedPhotocardsCount));

                        // load member names
                        List<String> memberNames = new ArrayList<>();
                        for (String photocardId : currentUserData.getOwnedPhotocardIds()) {
                            Photocard.loadPhotocardData(photocardId, new Photocard.PhotocardDataLoadListener() {
                                @Override
                                public void onPhotocardDataLoaded(Photocard photocard) {
                                    if (photocard != null) {
                                        String memberName = photocard.getMemberName();
                                        memberNames.add(memberName);
                                    }

                                    // check if all data has been loaded
                                    if (memberNames.size() == currentUserData.getOwnedPhotocardIds().size()) {
                                        updateMostPopularIdol(memberNames);
                                    }
                                }

                                @Override
                                public void onPhotocardDataLoadFailed(String error) {
                                    Toast.makeText(requireContext(), "Failed to load photocard data", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onUserDataLoadFailed(String error) {
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateMostPopularIdol(List<String> memberNames) {

        // no member names loaded or there are no photocards
        if (memberNames.isEmpty()) {
            favoriteIdolLabel.setText("N/A");
        }
        else {
            Map<String, Integer> memberOccurrences = new HashMap<>();
            for (String memberName : memberNames) {
                memberOccurrences.put(memberName, memberOccurrences.getOrDefault(memberName, 0) + 1);
            }

            int maxCount = Collections.max(memberOccurrences.values());
            long countOfMax = memberOccurrences.values().stream().filter(count -> count == maxCount).count();

            String mostPopularIdol = (countOfMax > 1) ? "N/A" : memberOccurrences.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() == maxCount)
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            favoriteIdolLabel.setText(mostPopularIdol);
        }
    }


    private void loadProfileImage(String fileName) {
        if (fileName != null) {
            try {
                File directory = new File(requireContext().getCacheDir(), "images");
                if (!directory.exists()) {
                    if (!directory.mkdirs()) {
                        Toast.makeText(requireContext(), "Failed to create directory", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // create a temporary file to store the downloaded image
                File localFile = new File(directory, fileName);

                // if a local file exists, load the image directly
                if (localFile.exists()) {
                    Glide.with(requireContext())
                            .load(localFile)
                            .placeholder(R.drawable.placeholderimage)
                            .into(profilePictureView);
                }
                else {
                    StorageReference gsReference = FirebaseStorage.getInstance().getReference().child("Users/" + fileName);

                    gsReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(requireContext())
                                .load(uri)
                                .placeholder(R.drawable.placeholderimage)
                                .into(profilePictureView);

                        gsReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                        }).addOnFailureListener(exception -> {
                            Toast.makeText(requireContext(), "Failed to save image locally", Toast.LENGTH_SHORT).show();
                        });

                    }).addOnFailureListener(exception -> {
                        Toast.makeText(requireContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}