package com.kubixdev.kollect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kubixdev.kollect.R;
import com.kubixdev.kollect.model.Photocard;
import java.io.File;
import java.util.List;

public class PhotocardAdapter extends RecyclerView.Adapter<PhotocardAdapter.ViewHolder> {
    private final Context context;
    private List<String> photocardIds;
    private final FirebaseStorage storage;

    public PhotocardAdapter(Context context, List<String> photocardIds) {
        this.context = context;
        this.photocardIds = photocardIds;
        this.storage = FirebaseStorage.getInstance();
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
    }


    @Override
    public int getItemCount() {
        return photocardIds.size();
    }


    public void setPhotocardIds(List<String> photocardIds) {
        this.photocardIds = photocardIds;
        notifyDataSetChanged();
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

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
        }
    }
}