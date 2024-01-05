package com.kubixdev.kollect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.adapters.PhotocardAdapter;
import com.kubixdev.kollect.model.User;
import java.util.ArrayList;

public class CollectionFragment extends Fragment {
    private RecyclerView photocardRecycler;
    private PhotocardAdapter photocardAdapter;
    private User currentUserData;

    public CollectionFragment() {}

    public void passUserData(User user) {
        this.currentUserData = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        RadioButton ownedButton = view.findViewById(R.id.ownedButton);
        RadioButton wishlistButton = view.findViewById(R.id.wishlistButton);
        photocardRecycler = view.findViewById(R.id.photocardRecycler);

        // sets up a grid on recyclerview
        int numberOfColumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        photocardRecycler.setLayoutManager(layoutManager);

        // sets a default selection for the radio button group
        ownedButton.setChecked(true);

        // initializes the adapter with an empty list (to be updated later)
        photocardAdapter = new PhotocardAdapter(getContext(), new ArrayList<>());
        photocardRecycler.setAdapter(photocardAdapter);

        // click listeners
        ownedButton.setOnClickListener(v -> updateAdapter(true));
        wishlistButton.setOnClickListener(v -> updateAdapter(false));

        // initially loads data when the fragment is created
        updateAdapter(true);

        return view;
    }

    private void updateAdapter(boolean isOwned) {
        if (currentUserData != null) {
            ArrayList<String> photocardIds;

            // checks which tab is currently clicked
            if (isOwned) {
                photocardIds = currentUserData.getOwnedPhotocardIds();
            }
            else {
                photocardIds = currentUserData.getWishlistPhotocardIds();
            }

            // passes the updated photocard ids to the adapter
            photocardAdapter.setPhotocardIds(photocardIds);

            // notifies about a data change (so that it redraws recyclerview)
            photocardAdapter.notifyDataSetChanged();

            // scrolls to the top automatically
            photocardRecycler.scrollToPosition(0);
        }
    }
}