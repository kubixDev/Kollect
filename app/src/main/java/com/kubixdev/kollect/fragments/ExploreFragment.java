package com.kubixdev.kollect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.adapters.ArtistAdapter;
import com.kubixdev.kollect.model.User;
import com.kubixdev.kollect.model.Artist;
import java.util.ArrayList;

public class ExploreFragment extends Fragment {
    private RecyclerView artistRecycler;
    private ArtistAdapter artistAdapter;
    private User currentUserData;

    public ExploreFragment(){}

    public void passUserData(User user) {
        this.currentUserData = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        artistRecycler = view.findViewById(R.id.artistRecycler);

        // sets up a grid on recyclerview
        int numberOfColumns = 1;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        artistRecycler.setLayoutManager(layoutManager);

        // initializes the adapter with an empty list (to be updated later)
        artistAdapter = new ArtistAdapter(getContext(), new ArrayList<>());
        artistRecycler.setAdapter(artistAdapter);

        // initially loads data when the fragment is created
        updateAdapter();

        return view;
    }

    private void updateAdapter() {
        Artist.loadAllArtists(new Artist.ArtistListLoadListener() {
            @Override
            public void onArtistListLoaded(ArrayList<String> artistNames) {
                artistAdapter.setArtistIds(artistNames);
                artistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onArtistListLoadFailed(String error) {
                Toast.makeText(getContext(), "Failed to load artist names", Toast.LENGTH_SHORT).show();
            }
        });

        artistRecycler.scrollToPosition(0);
    }
}