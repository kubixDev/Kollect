package com.kubixdev.kollect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.fragment.app.Fragment;
import com.kubixdev.kollect.R;

public class CollectionFragment extends Fragment {

    public CollectionFragment(){
        // require an empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        // default selection for radio button group
        RadioButton ownedButton = view.findViewById(R.id.ownedButton);
        ownedButton.setChecked(true);

        return view;
    }
}