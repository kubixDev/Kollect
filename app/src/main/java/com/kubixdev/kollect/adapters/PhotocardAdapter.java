package com.kubixdev.kollect.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.fragments.CollectionFragment;
import com.kubixdev.kollect.model.Photocard;
import com.kubixdev.kollect.model.User;
import com.kubixdev.kollect.utils.BlurUtils;
import java.io.File;
import java.util.List;

public class PhotocardAdapter extends RecyclerView.Adapter<PhotocardAdapter.ViewHolder> {
    private final Context context;
    private List<String> photocardIds;
    private final FirebaseStorage storage;
    private final int fragmentType;
    private final boolean isWishlistSelected;

    public PhotocardAdapter(Context context, List<String> photocardIds, int fragmentType, boolean isWishlistSelected) {
        this.context = context;
        this.photocardIds = photocardIds;
        this.storage = FirebaseStorage.getInstance();
        this.fragmentType = fragmentType;
        this.isWishlistSelected = isWishlistSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photocard_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String photocardId = photocardIds.get(position);

        // cancels any previously loaded photocards to prevent from glitching
        Glide.with(context).clear(holder.imageView);

        // retrieves photocard details from firestore
        Photocard.loadPhotocardData(photocardId, new Photocard.PhotocardDataLoadListener() {
            @Override
            public void onPhotocardDataLoaded(Photocard photocard) {

                // checks if the adapter is still bound to the correct view
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {

                    // displays images using Glide
                    if ((photocard != null) && (photocard.getImage() != null)) {
                        loadImage(holder.imageView, photocard.getImage());
                    }
                }
            }

            @Override
            public void onPhotocardDataLoadFailed(String error) {
                Toast.makeText(context.getApplicationContext(), "Failed to load photocard data", Toast.LENGTH_SHORT).show();
            }
        });

        // set long click listener to handle the visibility of the button layout
        holder.itemView.setOnLongClickListener(view -> {

            // toggle the visibility of the button layout
            holder.toggleButtonLayoutVisibility();
            vibrate(view.getContext());
            return true;
        });

        // click listeners for buttons inside the button layout
        holder.addToCollectionButton.setOnClickListener(v -> {
            addToCollection(photocardId);
            holder.hideButtonLayout();
            holder.unblurImage();
        });

        holder.addToWishlistButton.setOnClickListener(v -> {
            addToWishlist(photocardId);
            holder.hideButtonLayout();
            holder.unblurImage();
        });

        holder.removeButton.setOnClickListener(v -> {
            remove(photocardId, isWishlistSelected, holder.getAdapterPosition());
            holder.hideButtonLayout();
            holder.unblurImage();
        });

        holder.markAsOwnedButton.setOnClickListener(v -> {
            markAsOwned(photocardId, holder.getAdapterPosition());
            holder.hideButtonLayout();
            holder.unblurImage();
        });

        holder.cancelButton.setOnClickListener(v -> {
            holder.hideButtonLayout();
            holder.unblurImage();
        });

        // checks the fragment type to show the correct buttons
        if (fragmentType == 1) {

            // explore fragment
            holder.addToCollectionButton.setVisibility(View.VISIBLE);
            holder.addToWishlistButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.removeButton.setVisibility(View.GONE);
            holder.markAsOwnedButton.setVisibility(View.GONE);
        }
        else if (fragmentType == 2) {

            // collection fragment
            holder.addToCollectionButton.setVisibility(View.GONE);
            holder.addToWishlistButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.removeButton.setVisibility(View.VISIBLE);

            if (isWishlistSelected) {
                holder.markAsOwnedButton.setVisibility(View.VISIBLE);
            }
            else {
                holder.markAsOwnedButton.setVisibility(View.GONE);
            }
        }
    }


    private void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }


    @Override
    public int getItemCount() {
        return photocardIds.size();
    }


    public void setPhotocardIds(List<String> photocardIds) {
        this.photocardIds = photocardIds;
        notifyDataSetChanged();
    }


    public void removePhotocard(int position) {
        photocardIds.remove(position);
        notifyItemRemoved(position);
    }


    private void loadImage(ImageView imageView, String fileName) {
        StorageReference gsReference = storage.getReference().child("Photocards/" + fileName);

        try {
            File directory = new File(context.getCacheDir(), "images");
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    Toast.makeText(context, "Failed to create directory", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            File localFile = new File(directory, fileName);

            if (localFile.exists()) {
                Glide.with(context)
                        .load(localFile)
                        .placeholder(R.drawable.placeholderimage)
                        .into(imageView);
            }
            else {
                gsReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(context)
                            .load(uri)
                            .placeholder(R.drawable.placeholderimage)
                            .into(imageView);

                    gsReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    }).addOnFailureListener(exception -> {
                        Toast.makeText(context, "Failed to save image locally", Toast.LENGTH_SHORT).show();
                    });

                }).addOnFailureListener(exception -> {
                    Toast.makeText(context, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final LinearLayout addPhotocardLayout;
        final AppCompatButton addToCollectionButton, addToWishlistButton, cancelButton, removeButton, markAsOwnedButton;

        // keeps a reference to the original bitmap
        private Bitmap originalBitmap;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            addPhotocardLayout = view.findViewById(R.id.addPhotocardLayout);
            addToCollectionButton = view.findViewById(R.id.addToCollectionButton);
            addToWishlistButton = view.findViewById(R.id.addToWishlistButton);
            cancelButton = view.findViewById(R.id.cancelButton);
            removeButton = view.findViewById(R.id.removeButton);
            markAsOwnedButton = view.findViewById(R.id.markAsOwnedButton);

            // initially hides the button layout
            addPhotocardLayout.setVisibility(View.GONE);
        }

        void toggleButtonLayoutVisibility() {
            if (addPhotocardLayout.getVisibility() == View.VISIBLE) {
                hideButtonLayout();
                unblurImage();
            }
            else {
                addPhotocardLayout.setVisibility(View.VISIBLE);
                originalBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                BlurUtils.applyBlur(imageView.getContext(), imageView);
            }
        }

        void hideButtonLayout() {
            addPhotocardLayout.setVisibility(View.GONE);
            unblurImage();
        }

        void unblurImage() {
            BlurUtils.unblur(imageView, originalBitmap);
        }
    }

    // button action methods
    private void addToCollection(String photocardId) {
        User.loadUserData(FirebaseAuth.getInstance().getUid(), new User.UserDataLoadListener() {
            @Override
            public void onUserDataLoaded(User user) {
                if (user != null) {
                    if (user.getOwnedPhotocardIds().contains(photocardId)) {
                        Toast.makeText(context, "Already marked as owned", Toast.LENGTH_SHORT).show();
                    }
                    else if (user.getWishlistPhotocardIds().contains(photocardId)) {
                        Toast.makeText(context, "Already added to wishlist", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        user.addNewOwnedPhotocardId(photocardId);
                        Toast.makeText(context, "Added to collection", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onUserDataLoadFailed(String error) {
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToWishlist(String photocardId) {
        User.loadUserData(FirebaseAuth.getInstance().getUid(), new User.UserDataLoadListener() {
            @Override
            public void onUserDataLoaded(User user) {
                if (user != null) {
                    if (user.getOwnedPhotocardIds().contains(photocardId)) {
                        Toast.makeText(context, "Already marked as owned", Toast.LENGTH_SHORT).show();
                    }
                    else if (user.getWishlistPhotocardIds().contains(photocardId)) {
                        Toast.makeText(context, "Already added to wishlist", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        user.addNewWishlistPhotocardId(photocardId);
                        Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onUserDataLoadFailed(String error) {
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void remove(String photocardId, boolean isWishlistSelected, int position) {
        User.loadUserData(FirebaseAuth.getInstance().getUid(), new User.UserDataLoadListener() {
            @Override
            public void onUserDataLoaded(User user) {
                if (user != null) {
                    if (isWishlistSelected) {
                        user.removeWishlistPhotocardById(photocardId);
                    }
                    else {
                        user.removeOwnedPhotocardById(photocardId);
                    }

                    removePhotocard(position);
                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUserDataLoadFailed(String error) {
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAsOwned(String photocardId, int position) {
        User.loadUserData(FirebaseAuth.getInstance().getUid(), new User.UserDataLoadListener() {
            @Override
            public void onUserDataLoaded(User user) {
                if (user != null) {
                    if (user.getOwnedPhotocardIds().contains(photocardId)) {
                        Toast.makeText(context, "Already marked as owned", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        user.addNewOwnedPhotocardId(photocardId);
                        user.removeWishlistPhotocardById(photocardId);
                        removePhotocard(position);

                        Toast.makeText(context, "Marked as owned", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onUserDataLoadFailed(String error) {
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}