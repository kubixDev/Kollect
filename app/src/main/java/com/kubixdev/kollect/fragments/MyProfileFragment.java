package com.kubixdev.kollect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.activities.LoginActivity;
import com.kubixdev.kollect.model.User;
import com.kubixdev.kollect.utils.BlurUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProfileFragment extends Fragment {
    FirebaseAuth mAuth;
    AppCompatButton logoutButton;
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

        mAuth = FirebaseAuth.getInstance();
        logoutButton = view.findViewById(R.id.logoutButton);
        userNameLabel = view.findViewById(R.id.userNameLabel);

        // handle user logout (only temporary)
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        userNameLabel.setText(currentUserData.getEmail());

        // blurs background image
        ImageView backgroundPictureView = view.findViewById(R.id.backgroundPictureView);
        BlurUtils.applyBlur(requireContext(), backgroundPictureView);

        return view;
    }
}