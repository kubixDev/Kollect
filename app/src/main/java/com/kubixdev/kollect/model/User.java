package com.kubixdev.kollect.model;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String accountType;
    private String accountCreationDate;
    private String profileImage;
    private ArrayList<String> ownedPhotocardIds;
    private ArrayList<String> wishlistPhotocardIds;
    private ArrayList<String> friendListIds;

    // default constructor
    public User() {
    }

    // constructor with parameters
    public User(String username, String accountType, String accountCreationDate, String profileImage,
                ArrayList<String> ownedPhotocardIds, ArrayList<String> wishlistPhotocardIds,
                ArrayList<String> friendListIds) {
        this.username = username;
        this.accountType = accountType;
        this.accountCreationDate = accountCreationDate;
        this.profileImage = profileImage;
        this.ownedPhotocardIds = ownedPhotocardIds;
        this.wishlistPhotocardIds = wishlistPhotocardIds;
        this.friendListIds = friendListIds;
    }

    public String getUsername() {
        return username;
    }

    // update the database with new data
    public void setUsername(String username) {
        this.username = username;
        updateUserData("username", username);
    }

    public String getAccountType() {
        return accountType;
    }

    // update the database with new data
    public void setAccountType(String accountType) {
        this.accountType = accountType;
        updateUserData("accountType", accountType);
    }

    public String getAccountCreationDate() {
        return accountCreationDate;
    }

    public void setAccountCreationDate(String accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    public ArrayList<String> getOwnedPhotocardIds() {
        return ownedPhotocardIds;
    }

    public void setOwnedPhotocardIds(ArrayList<String> ownedPhotocardIds) {
        this.ownedPhotocardIds = ownedPhotocardIds;
    }

    // update the database with new data
    public void addNewOwnedPhotocardId(String photocardId) {
        ownedPhotocardIds.add(photocardId);
        updateUserData("ownedPhotocardIds", ownedPhotocardIds);
    }

    // update the database with new data
    public void removeOwnedPhotocardById(String photocardId) {
        ownedPhotocardIds.remove(photocardId);
        updateUserData("ownedPhotocardIds", ownedPhotocardIds);
    }

    public ArrayList<String> getWishlistPhotocardIds() {
        return wishlistPhotocardIds;
    }

    public void setWishlistPhotocardIds(ArrayList<String> wishlistPhotocardIds) {
        this.wishlistPhotocardIds = wishlistPhotocardIds;
    }

    // update the database with new data
    public void addNewWishlistPhotocardId(String photocardId) {
        wishlistPhotocardIds.add(photocardId);
        updateUserData("wishlistPhotocardIds", wishlistPhotocardIds);
    }

    // update the database with new data
    public void removeWishlistPhotocardById(String photocardId) {
        wishlistPhotocardIds.remove(photocardId);
        updateUserData("wishlistPhotocardIds", wishlistPhotocardIds);
    }

    public ArrayList<String> getFriendListIds() {
        return friendListIds;
    }

    public void setFriendListIds(ArrayList<String> friendListIds) {
        this.friendListIds = friendListIds;
    }

    // update the database with new data
    public void addNewFriendId(String friendId) {
        friendListIds.add(friendId);
        updateUserData("friendListIds", friendListIds);
    }

    // update the database with new data
    public void removeFriendById(String friendId) {
        friendListIds.remove(friendId);
        updateUserData("friendListIds", friendListIds);
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        updateUserData("profileImage", profileImage);
    }

    // static method to retrieve user data from firestore for any user
    public static void loadUserData(String userId, final UserDataLoadListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            User user = document.toObject(User.class);

                            if (user != null) {
                                listener.onUserDataLoaded(user);
                            }
                            else {
                                listener.onUserDataLoadFailed("Failed to convert document to User object");
                                Log.e("USERINFO", "Failed to convert document to User object");
                            }

                        }
                        else {
                            listener.onUserDataLoadFailed("No such document");
                            Log.e("USERINFO", "No such document");
                        }

                    }
                    else {
                        listener.onUserDataLoadFailed("Task failed with exception: " + task.getException());
                        Log.e("USERINFO", "Task failed with exception: " + task.getException());
                    }
                });
    }


    // interface for callbacks when user data is loaded
    public interface UserDataLoadListener {
        void onUserDataLoaded(User user);
        void onUserDataLoadFailed(String error);
    }


    // helper method to update user data in firestore (with the currently logged user ID because you can only update your data)
    private void updateUserData(String fieldName, Object value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference userRef = db.collection("users").document(currentUser.getUid());

        Map<String, Object> updates = new HashMap<>();
        updates.put(fieldName, value);

        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // update successful
                })
                .addOnFailureListener(e -> {
                    // handle failure
                });
    }
}