package com.kubixdev.kollect.model;

import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class Member {
    private String firstName;
    private String lastName;
    private String artistId;
    private ArrayList<String> photocardIds;
    private String image;

    // default constructor
    public Member() {
    }

    // constructor with parameters
    public Member(String firstName, String lastName, String artistId, ArrayList<String> photocardIds, String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.artistId = artistId;
        this.photocardIds = photocardIds;
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public ArrayList<String> getPhotocardIds() {
        return photocardIds;
    }

    public void setPhotocardIds(ArrayList<String> photocardIds) {
        this.photocardIds = photocardIds;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // static method to retrieve member data from firestore for any member
    public static void loadMemberData(String memberName, final Member.MemberDataLoadListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("members")
                .document(memberName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Member member = document.toObject(Member.class);

                            if (member != null) {
                                listener.onMemberDataLoaded(member);
                            } else {
                                listener.onMemberDataLoadFailed("Failed to convert document to Member object");
                                Log.e("MEMBERINFO", "Failed to convert document to Member object");
                            }

                        } else {
                            listener.onMemberDataLoadFailed("No such document");
                            Log.e("MEMBERINFO", "No such document");
                        }

                    } else {
                        listener.onMemberDataLoadFailed("Task failed with exception: " + task.getException());
                        Log.e("MEMBERINFO", "Task failed with exception: " + task.getException());
                    }
                });
    }


    public static void loadMembersForArtist(String artistName, final Member.MemberListLoadListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("members")
                .whereEqualTo("artistId", artistName)
                .orderBy("artistId")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> memberNames = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            memberNames.add(document.getId());
                        }

                        listener.onMemberListLoaded(memberNames);
                    } else {
                        listener.onMemberListLoadFailed("Failed to load members for artist: " + artistName);
                        Log.e("MEMBERINFO", "Failed to load members for artist: " + artistName);
                    }
                });
    }


    // interface for callbacks when member data is loaded
    public interface MemberDataLoadListener {
        void onMemberDataLoaded(Member member);

        void onMemberDataLoadFailed(String error);
    }

    // interface for callbacks when member list is loaded
    public interface MemberListLoadListener {
        void onMemberListLoaded(ArrayList<String> memberNames);

        void onMemberListLoadFailed(String error);
    }
}