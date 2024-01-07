package com.kubixdev.kollect.model;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Artist {
    private ArrayList<String> memberIds;
    private String image;

    // default constructor
    public Artist() {
    }

    // constructor with parameters
    public Artist(ArrayList<String> memberIds, String image) {
        this.memberIds = memberIds;
        this.image = image;
    }

    public ArrayList<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(ArrayList<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // static method to retrieve artist data from firestore for any artist
    public static void loadArtistData(String artistName, final Artist.ArtistDataLoadListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("artists")
                .document(artistName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Artist artist = document.toObject(Artist.class);

                            if (artist != null) {
                                listener.onArtistDataLoaded(artist);
                            }
                            else {
                                listener.onArtistDataLoadFailed("Failed to convert document to Artist object");
                                Log.e("ARTISTINFO", "Failed to convert document to Artist object");
                            }

                        }
                        else {
                            listener.onArtistDataLoadFailed("No such document");
                            Log.e("ARTISTINFO", "No such document");
                        }

                    }
                    else {
                        listener.onArtistDataLoadFailed("Task failed with exception: " + task.getException());
                        Log.e("ARTISTINFO", "Task failed with exception: " + task.getException());
                    }
                });
    }

    // static method to retrieve artist names from firestore
    public static void loadAllArtists(ArtistListLoadListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("artists")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> artistNames = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            artistNames.add(document.getId());
                        }

                        listener.onArtistListLoaded(artistNames);
                    }
                    else {
                        listener.onArtistListLoadFailed("Task failed with exception: " + task.getException());
                        Log.e("ARTISTINFO", "Task failed with exception: " + task.getException());
                    }
                });
    }


    // interface for callbacks when artist data is loaded
    public interface ArtistDataLoadListener {
        void onArtistDataLoaded(Artist artist);
        void onArtistDataLoadFailed(String error);
    }

    // interface for callbacks when artists names are loaded
    public interface ArtistListLoadListener {
        void onArtistListLoaded(ArrayList<String> artistNames);
        void onArtistListLoadFailed(String error);
    }
}