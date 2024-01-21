package com.kubixdev.kollect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.adapters.ArtistAdapter;
import com.kubixdev.kollect.adapters.MemberAdapter;
import com.kubixdev.kollect.adapters.PhotocardAdapter;
import com.kubixdev.kollect.model.User;
import com.kubixdev.kollect.model.Artist;
import com.kubixdev.kollect.model.Member;
import com.kubixdev.kollect.model.Photocard;
import java.util.ArrayList;

public class ExploreFragment extends Fragment implements ArtistAdapter.ArtistClickListener, MemberAdapter.MemberClickListener {
    private LinearLayout searchLayoutButton;
    private RecyclerView mainRecycler;
    private TextView exploreLabel;
    private ArtistAdapter artistAdapter;
    private MemberAdapter memberAdapter;
    private PhotocardAdapter photocardAdapter;
    private User currentUserData;

    public ExploreFragment() {}

    public void passUserData(User user) {
        this.currentUserData = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        mainRecycler = view.findViewById(R.id.mainRecycler);
        searchLayoutButton = view.findViewById(R.id.searchLayoutButton);
        exploreLabel = view.findViewById(R.id.exploreLabel);

        // sets up a grid on recyclerview
        int numberOfColumns = 1;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        mainRecycler.setLayoutManager(layoutManager);

        // initializes the artistAdapter with an empty list (to be updated later)
        artistAdapter = new ArtistAdapter(getContext(), new ArrayList<>(), this);
        mainRecycler.setAdapter(artistAdapter);

        // initializes the memberAdapter with an empty list (to be updated later)
        memberAdapter = new MemberAdapter(getContext(), new ArrayList<>(), this);

        // initializes the photocardAdapter with an empty list (to be updated later)
        photocardAdapter = new PhotocardAdapter(getContext(), new ArrayList<>());

        // initially loads data when the fragment is created
        updateAdapter();

        // creates a click listener to run search activity
//        searchLayoutButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), SearchActivity.class);
//            startActivity(intent);
//            getActivity().overridePendingTransition(0, 0);
//        });

        // handles back button press functionality on the recyclerview
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                // going back from photocards to members
                if (mainRecycler.getAdapter() == photocardAdapter) {
                    mainRecycler.setAdapter(memberAdapter);

                    // updates the column number
                    int numberOfColumns = 1;
                    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
                    mainRecycler.setLayoutManager(layoutManager);

                    // updates the label above recyclerview
                    exploreLabel.setText("Explore members");

                }

                // going back from members to artists
                else if (mainRecycler.getAdapter() == memberAdapter) {
                    mainRecycler.setAdapter(artistAdapter);
                    exploreLabel.setText("Explore artists");
                }

                // going back from artists moves the app to background
                else {
                    if (getActivity() != null) {
                        getActivity().moveTaskToBack(true);
                    }
                }
            }
        };

        // adds the callback to the back button handler
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

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

        mainRecycler.scrollToPosition(0);
    }

    @Override
    public void onArtistClicked(String artistName) {

        // updates the label above recyclerview
        exploreLabel.setText("Explore members");

        Member.loadMembersForArtist(artistName, new Member.MemberListLoadListener() {
            @Override
            public void onMemberListLoaded(ArrayList<String> memberIds) {
                memberAdapter.setMemberIds(memberIds);
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMemberListLoadFailed(String error) {
                Toast.makeText(getContext(), "Failed to load members", Toast.LENGTH_SHORT).show();
            }
        });

        mainRecycler.setAdapter(memberAdapter);
    }

    @Override
    public void onMemberClicked(String memberId) {

        // updates the label above recyclerview
        exploreLabel.setText("Explore photocards");

        Photocard.loadPhotocardsForMember(memberId, new Photocard.PhotocardListLoadListener() {
            @Override
            public void onPhotocardListLoaded(ArrayList<String> photocardIds) {
                photocardAdapter.setPhotocardIds(photocardIds);
                photocardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPhotocardListLoadFailed(String error) {
                Toast.makeText(getContext(), "Failed to load photocards", Toast.LENGTH_SHORT).show();
            }
        });

        // updates the column number for photocard layout
        int numberOfColumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        mainRecycler.setLayoutManager(layoutManager);

        mainRecycler.setAdapter(photocardAdapter);
    }
}