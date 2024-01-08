package com.kubixdev.kollect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.activities.SettingsActivity;
import com.kubixdev.kollect.model.User;
import com.kubixdev.kollect.utils.BlurUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProfileFragment extends Fragment {
    AppCompatButton settingsButton;
    TextView userNameLabel;

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

        // blurs background image
        ImageView backgroundPictureView = view.findViewById(R.id.backgroundPictureView);
        BlurUtils.applyBlur(requireContext(), backgroundPictureView);

        // handle settings activity button
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        });

        return view;
    }
}