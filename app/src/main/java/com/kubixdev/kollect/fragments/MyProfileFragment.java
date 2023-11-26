package com.kubixdev.kollect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.utils.BlurUtils;
import android.widget.ImageView;

public class MyProfileFragment extends Fragment {

    public MyProfileFragment(){
        // require an empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        // blurring the background image
        ImageView backgroundPictureView = view.findViewById(R.id.backgroundPictureView);
        BlurUtils.applyBlur(requireContext(), backgroundPictureView);

        return view;
    }
}
