package com.kubixdev.kollect.model;

import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class Photocard {
    private String artistName;
    private String memberName;
    private String image;

    // default constructor
    public Photocard() {
    }

    // constructor with parameters
    public Photocard(String artistName, String memberName, String image) {
        this.artistName = artistName;
        this.memberName = memberName;
        this.image = image;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // static method to retrieve photocard data from firestore for any photocard
    public static void loadPhotocardData(String photocardId, final Photocard.PhotocardDataLoadListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("photocards")
                .document(photocardId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Photocard photocard = document.toObject(Photocard.class);

                            if (photocard != null) {
                                listener.onPhotocardDataLoaded(photocard);
                            }
                            else {
                                listener.onPhotocardDataLoadFailed("Failed to convert document to Photocard object");
                                Log.e("PHOTOCARDINFO", "Failed to convert document to Photocard object");
                            }

                        }
                        else {
                            listener.onPhotocardDataLoadFailed("No such document");
                            Log.e("PHOTOCARDINFO", "No such document");
                        }

                    }
                    else {
                        listener.onPhotocardDataLoadFailed("Task failed with exception: " + task.getException());
                        Log.e("PHOTOCARDINFO", "Task failed with exception: " + task.getException());
                    }
                });
    }


    public static void loadPhotocardsForMember(String memberId, final PhotocardListLoadListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("photocards")
                .whereEqualTo("memberName", memberId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> photocardIds = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            photocardIds.add(document.getId());
                        }

                        listener.onPhotocardListLoaded(photocardIds);
                    }
                    else {
                        listener.onPhotocardListLoadFailed("Failed to load photocards for member: " + memberId);
                        Log.e("PHOTOCARDINFO", "Failed to load photocards for member: " + memberId);
                    }
                });
    }


    // interface for callbacks when photocard data is loaded
    public interface PhotocardDataLoadListener {
        void onPhotocardDataLoaded(Photocard photocard);
        void onPhotocardDataLoadFailed(String error);
    }


    // interface for callbacks when photocard list is loaded
    public interface PhotocardListLoadListener  {
        void onPhotocardListLoaded(ArrayList<String> photocardIds);
        void onPhotocardListLoadFailed(String error);
    }
}