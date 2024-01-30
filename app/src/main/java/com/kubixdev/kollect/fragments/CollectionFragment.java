package com.kubixdev.kollect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.adapters.PhotocardAdapter;
import com.kubixdev.kollect.model.User;
import java.util.ArrayList;

public class CollectionFragment extends Fragment {
    private RecyclerView photocardRecycler;
    private PhotocardAdapter photocardAdapter;
    private User currentUserData;
    RadioButton ownedButton;
    RadioButton wishlistButton;

    public CollectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        ownedButton = view.findViewById(R.id.ownedButton);
        wishlistButton = view.findViewById(R.id.wishlistButton);
        photocardRecycler = view.findViewById(R.id.photocardRecycler);

        // sets up a grid on recyclerview
        int numberOfColumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        photocardRecycler.setLayoutManager(layoutManager);

        // sets a default selection for the radio button group
        ownedButton.setChecked(true);

        // click listeners
        ownedButton.setOnClickListener(v -> updateAdapter(true));
        wishlistButton.setOnClickListener(v -> updateAdapter(false));

        // initially loads data when the fragment is created
        updateAdapter(true);

        return view;
    }

    private void updateAdapter(boolean isOwned) {
        User.loadUserData(FirebaseAuth.getInstance().getUid(), new User.UserDataLoadListener() {
            @Override
            public void onUserDataLoaded(User user) {
                currentUserData = user;

                if (currentUserData != null) {
                    ArrayList<String> photocardIds;

                    // checks which tab is currently clicked
                    if (isOwned) {
                        photocardIds = currentUserData.getOwnedPhotocardIds();
                    }
                    else {
                        photocardIds = currentUserData.getWishlistPhotocardIds();
                    }

                    // initializes the adapter
                    photocardAdapter = new PhotocardAdapter(getContext(), photocardIds, 2, isWishlistButtonSelected());
                    photocardRecycler.setAdapter(photocardAdapter);

                    // notifies about a data change (so that it redraws recyclerview)
                    photocardAdapter.notifyDataSetChanged();

                    // scrolls to the top automatically
                    photocardRecycler.scrollToPosition(0);
                }
            }

            @Override
            public void onUserDataLoadFailed(String error) {}
        });
    }

    public boolean isWishlistButtonSelected() {
        return wishlistButton.isChecked();
    }

    @Override
    public void onResume() {
        super.onResume();

        // make sure that ownedButton is selected by default
        ownedButton.setChecked(true);
        wishlistButton.setChecked(false);
        updateAdapter(true);
    }
}