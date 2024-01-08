package com.kubixdev.kollect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.activities.SearchActivity;
import com.kubixdev.kollect.adapters.ArtistAdapter;
import com.kubixdev.kollect.adapters.MemberAdapter;
import com.kubixdev.kollect.model.User;
import com.kubixdev.kollect.model.Artist;
import com.kubixdev.kollect.model.Member;
import java.util.ArrayList;

public class ExploreFragment extends Fragment implements ArtistAdapter.ArtistClickListener {
    private LinearLayout searchLayoutButton;
    private RecyclerView artistRecycler;
    private ArtistAdapter artistAdapter;
    private MemberAdapter memberAdapter;
    private User currentUserData;

    public ExploreFragment(){}

    public void passUserData(User user) {
        this.currentUserData = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        artistRecycler = view.findViewById(R.id.artistRecycler);
        searchLayoutButton = view.findViewById(R.id.searchLayoutButton);

        // sets up a grid on recyclerview
        int numberOfColumns = 1;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        artistRecycler.setLayoutManager(layoutManager);

        // initializes the artistAdapter with an empty list (to be updated later)
        artistAdapter = new ArtistAdapter(getContext(), new ArrayList<>(), this);
        artistRecycler.setAdapter(artistAdapter);

        // initializes the memberAdapter with an empty list (to be updated later)
        memberAdapter = new MemberAdapter(getContext(), new ArrayList<>());

        // initially loads data when the fragment is created
        updateAdapter();

        // creates a click listener to run search activity
        searchLayoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        });

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

    @Override
    public void onArtistClicked(String artistName) {
        Member.loadMembersForArtist(artistName, new Member.MemberListLoadListener() {
            @Override
            public void onMemberListLoaded(ArrayList<String> memberNames) {
                memberAdapter.setMemberIds(memberNames);
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMemberListLoadFailed(String error) {
                Toast.makeText(getContext(), "Failed to load member names", Toast.LENGTH_SHORT).show();
            }
        });

        artistRecycler.setAdapter(memberAdapter);
    }
}